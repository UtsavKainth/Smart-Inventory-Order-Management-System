
document.addEventListener("DOMContentLoaded", loadProducts);

async function loadProducts() {
    const table = document.getElementById("productTable");

    try {
        const response = await fetch("api/products");

        if (!response.ok) {
            throw new Error("Server returned HTTP " + response.status);
        }

        const products = await response.json();

        let totalStock = 0;
        let inventoryValue = 0;

        table.innerHTML = "";

        products.forEach(product => {
            totalStock += product.stock;
            inventoryValue += product.price * product.stock;

            const row = document.createElement("tr");

            const values = [
                product.id,
                product.name,
                product.category,
                product.supplier,
                "₹" + Number(product.price).toLocaleString("en-IN"),
                product.stock
            ];

            values.forEach(value => {
                const cell = document.createElement("td");
                cell.textContent = value;
                row.appendChild(cell);
            });

            const statusCell = document.createElement("td");
            const badge = document.createElement("span");

            badge.textContent = product.status;
            badge.className = "status " +
                (product.status === "LOW STOCK"
                    ? "low-stock"
                    : "available");

            statusCell.appendChild(badge);
            row.appendChild(statusCell);
            table.appendChild(row);
        });

        if (products.length === 0) {
            const row = document.createElement("tr");
            const cell = document.createElement("td");
            cell.colSpan = 7;
            cell.textContent = "No products found.";
            row.appendChild(cell);
            table.appendChild(row);
        }

        document.getElementById("totalProducts").textContent =
            products.length;

        document.getElementById("totalStock").textContent =
            totalStock.toLocaleString("en-IN");

        document.getElementById("inventoryValue").textContent =
            "₹" + inventoryValue.toLocaleString("en-IN");

    } catch (error) {
        console.error("Inventory loading failed:", error);

        table.innerHTML = "";

        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 7;
        cell.textContent =
            "Unable to load inventory. Check the server connection.";

        row.appendChild(cell);
        table.appendChild(row);
    }
}

async function loadOrders() {
    const table = document.getElementById("ordersTable");
    if (!table) return;

    try {
        const response = await fetch("api/orders");
        if (!response.ok) {
            throw new Error("Unable to fetch orders");
        }

        const orders = await response.json();
        table.innerHTML = "";

        if (orders.length === 0) {
            const row = table.insertRow();
            const cell = row.insertCell();
            cell.colSpan = 7;
            cell.textContent = "No orders placed yet.";
            return;
        }

        orders.forEach(order => {
            const row = table.insertRow();

            [
                order.orderId,
                order.customer,
                order.product,
                order.quantity,
                "₹" + Number(order.price).toLocaleString("en-IN"),
                "₹" + Number(order.total).toLocaleString("en-IN"),
                order.status
            ].forEach(value => {
                const cell = row.insertCell();
                cell.textContent = value;
            });
        });

    } catch (error) {
        console.error(error);
        table.innerHTML =
            '<tr><td colspan="7">Unable to load orders.</td></tr>';
    }
}

async function loadProductOptions() {
    const select = document.getElementById("productId");
    if (!select) return;

    try {
        const response = await fetch("api/products");
        if (!response.ok) {
            throw new Error("Unable to fetch products");
        }

        const products = await response.json();
        select.innerHTML =
            '<option value="">Select Product</option>';

        products.forEach(product => {
            const option = document.createElement("option");
            option.value = product.id;
            option.textContent =
                product.name + " (Stock: " + product.stock + ")";
            option.disabled = product.stock <= 0;
            select.appendChild(option);
        });

    } catch (error) {
        console.error(error);
        select.innerHTML =
            '<option value="">Products unavailable</option>';
    }
}

async function placeOrder(event) {
    event.preventDefault();

    const button = document.getElementById("placeOrderBtn");
    const message = document.getElementById("orderMessage");

    const customerId = document.getElementById("customerId").value;
    const productId = document.getElementById("productId").value;
    const quantity = document.getElementById("quantity").value;

    if (!customerId || !productId || Number(quantity) < 1) {
        message.textContent = "Please enter valid order details.";
        return;
    }

    button.disabled = true;
    message.textContent = "Placing order...";

    try {
        const params = new URLSearchParams({
            customerId,
            productId,
            quantity
        });

        const response = await fetch("api/orders", {
            method: "POST",
            headers: {
                "Content-Type":
                    "application/x-www-form-urlencoded"
            },
            body: params.toString()
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.error || "Order failed");
        }

        message.textContent = "Order placed successfully!";
        document.getElementById("orderForm").reset();


await Promise.all([
    loadProducts(),
    loadOrders(),
    loadProductOptions(),
    loadAuditHistory()
]);


    } catch (error) {
        message.textContent = error.message;
    } finally {
        button.disabled = false;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    loadOrders();
    loadProductOptions();

    const form = document.getElementById("orderForm");
    if (form) {
        form.addEventListener("submit", placeOrder);
    }
});


async function loadAuditHistory() {
    const table = document.getElementById("auditTable");
    if (!table) return;

    try {
        const response = await fetch("api/audit");

        if (!response.ok) {
            throw new Error("Unable to fetch stock audit records");
        }

        const records = await response.json();
        table.innerHTML = "";

        if (records.length === 0) {
            const row = table.insertRow();
            const cell = row.insertCell();
            cell.colSpan = 7;
            cell.textContent = "No stock changes recorded yet.";
            return;
        }

        records.forEach(record => {
            const row = table.insertRow();
            const change = record.newStock - record.oldStock;

            const values = [
                record.auditId,
                record.product,
                record.oldStock,
                record.newStock,
                change > 0 ? "+" + change : String(change),
                record.changeDate,
                record.changedBy
            ];

            values.forEach((value, index) => {
                const cell = row.insertCell();
                cell.textContent = value;

                if (index === 4) {
                    cell.classList.add(
                        change > 0 ? "stock-increase" :
                        change < 0 ? "stock-decrease" :
                        "stock-unchanged"
                    );
                }
            });
        });

    } catch (error) {
        console.error("Audit history error:", error);

        table.innerHTML =
            '<tr><td colspan="7">' +
            'Unable to load stock audit records.' +
            '</td></tr>';
    }
}

document.addEventListener("DOMContentLoaded", () => {
    loadAuditHistory();
});
