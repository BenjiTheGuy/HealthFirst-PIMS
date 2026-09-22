CREATE DATABASE pharmacy_db;

USE pharmacy_db;

-- Table 1: User --
CREATE TABLE users (
	user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Cashier') NOT NULL,
    full_name VARCHAR(100) NOT NULL
);

-- Table 2: Supplier --
CREATE TABLE suppliers (
	supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT
);

-- Table 3: Medicine --
CREATE TABLE medicines (
	medicine_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    company VARCHAR(100),
    medicine_type VARCHAR(50),
    price DECIMAL(10, 2) NOT NULL,
    quantity_in_stock INT NOT NULL,
    reorder_level INT NOT NULL,
    expiry_date DATE,
    supplier_id INT,
    
    FOREIGN KEY (supplier_id)
		REFERENCES suppliers(supplier_id)
);

-- Table 4: Sale --
CREATE TABLE sales (
	sale_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    user_id INT,
    
    FOREIGN KEY (user_id)
		REFERENCES users(user_id)
);

-- Table 5: Sale Items --
CREATE TABLE sale_items (
	sale_item_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    medicine_id INT NOT NULL,
    quantity_sold INT NOT NULL,
    price_at_sale DECIMAL(10, 2) NOT NULL,
    
    FOREIGN KEY (sale_id)
		REFERENCES sales(sale_id),
	
    FOREIGN KEY (medicine_id)
		REFERENCES medicines(medicine_id)
);

-- Inserting Into Tables --
INSERT INTO users (username, password, role, full_name)
VALUES
('admin', 'admin123', 'Admin', 'Benjamin Montague'),
('cashier', 'cashier123', 'Cashier', 'Hanan Ramanna');

INSERT INTO suppliers (name, contact_person, phone, email, address)
VALUES
('ABC Pharmaceuticals', 'Owen Wilson', '0311234567', 'abc@pharm.co.za', '23 Glenwood Road, Durban, South Africa'),
('Aspen Pharmacy', 'Terena Pillay', '0314536786', 'sales@aspen.co.za', '10 Dlamini Street, Durban, KwaZulu-Natal'),
('Clicks Pharmacy', 'Sarah Naidoo', '0353586835', 'orders@clicks.co.za', '54 Felix Road, Johannesburg, Gauteng'),
('Dis-Chem', 'Ishvah Pillay', '0353482193', 'chem@co.za', '45 Palmview Road, Cape Town, Western Cape');

INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id)
VALUES
('Myprodol', 'Aspen Pharmacy', 'Capsule', 89.99, 15, 20, '2027-02-15', 2),
('Grand-Pa', 'Clicks Pharmacy', 'Syrup', 52.99, 50, 10, '2028-08-23', 3),
('Panado', 'ABC Pharmaceuticals', 'Tablet', '39.99', 100, 20, '2026-09-25', 1),
('Fever Injection', 'Dis-Chem', 'Injection', 250.00, 7, 10, '2026-10-05', 4),
('Benylin', 'Clicks Pharmacy', 'Syrup', 89.99, 30, 5, '2027-05-24', 3);