PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS employee (
                                        id_employee TEXT PRIMARY KEY,
                                        name VARCHAR(100),
                                        role VARCHAR(50),
                                        password VARCHAR(100),
                                        bank_name text,
                                        num_account text
);

CREATE TABLE IF NOT EXISTS food (
                                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                                    name TEXT,
                                    price REAL
);

CREATE TABLE IF NOT EXISTS orders (
                                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      total_price REAL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS order_items (
                                           id INTEGER PRIMARY KEY AUTOINCREMENT,
                                           order_id INTEGER,
                                           food_id INTEGER,
                                           quantity INTEGER,
                                           FOREIGN KEY (order_id) REFERENCES orders(id),
                                           FOREIGN KEY (food_id) REFERENCES food(id)
);

CREATE TABLE IF NOT EXISTS payment_info (
                                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                                            account_number TEXT,
                                            bank_name TEXT
);
CREATE TABLE IF NOT EXISTS app_settings(
    key text primary key,
    value text
);
CREATE TABLE IF NOT EXISTS bank_info(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    short_name text, bin_code text
);

CREATE TABLE IF NOT EXISTS time_sheet(
    id_employee text,
    time_login date default CURRENT_DATE,
    FOREIGN KEY (id_employee) REFERENCES employee(id_employee)

);

INSERT INTO employee values('admin','admin','Manager','admin', 'Bank of America', '1234567890');

