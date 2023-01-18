-- Active: 1670016658658@@localhost@3306@research_center
CREATE DATABASE research_center;

USE research_center;

CREATE TABLE personnel (
	personnel_id INT NOT NULL AUTO_INCREMENT,
	full_name VARCHAR(20) NOT NULL,
	role ENUM ("Director", "Inventory Manager", "Researcher") NOT NULL,
	login VARCHAR(20) NOT NULL UNIQUE,
	password VARCHAR(20) NOT NULL,
	PRIMARY KEY (personnel_id)
);

CREATE TABLE researcher (
	researcher_id INT,
	experiment_id INT,
	PRIMARY KEY (researcher_id),
	FOREIGN KEY (researcher_id) REFERENCES personnel(personnel_id)
);

CREATE TABLE field (
	field_designation VARCHAR(30),
	PRIMARY KEY (field_designation)
);

CREATE TABLE specialty (
	researcher_id INT,
	field_designation VARCHAR(30),
	PRIMARY KEY (researcher_id, field_designation)
);

CREATE TABLE lab (
	lab_id INT AUTO_INCREMENT,
	location VARCHAR(50) NOT NULL,
	PRIMARY KEY (lab_id)
);

CREATE TABLE experiment (
	experiment_id INT AUTO_INCREMENT,
	researcher_id INT NOT NULL,
	state ENUM ("Suggested", "Denied", "Ongoing", "Concluded") NOT NULL,
	code_name VARCHAR(20),
	description TEXT NOT NULL,
	lab_id INT UNIQUE,
	PRIMARY KEY (experiment_id),
	FOREIGN KEY (researcher_id) REFERENCES researcher(researcher_id),
	FOREIGN KEY (lab_id) REFERENCES lab(lab_id)
);

ALTER TABLE researcher 
ADD FOREIGN KEY (experiment_id) REFERENCES experiment(experiment_id);

CREATE TABLE material_type (
	material_designation VARCHAR(30),
	stocked_quantity DOUBLE NOT NULL,
	PRIMARY KEY (material_designation)
);

CREATE TABLE equipment_type (
	equipment_designation VARCHAR(30),
	PRIMARY KEY (equipment_designation)
);

CREATE TABLE equipment (
	equipment_id INT AUTO_INCREMENT,
	equipment_designation VARCHAR(30) NOT NULL,
	experiment_id INT,
	PRIMARY KEY (equipment_id),
	FOREIGN KEY (experiment_id) REFERENCES experiment(experiment_id)
);

CREATE TABLE request (
	request_id INT AUTO_INCREMENT,
	state BOOLEAN,
	type ENUM ("Researcher", "Material", "Equipment") NOT NULL,
	experiment_id ENUM ("Pending", "Denied", "Approved") NOT NULL,
	field_designation VARCHAR(30),
	material_designation VARCHAR(30),
	equipment_designation VARCHAR(30),
	quantity DOUBLE,
	details TEXT,
	PRIMARY KEY (request_id),
	FOREIGN KEY (experiment_id) REFERENCES experiment(experiment_id),
	FOREIGN KEY (field_designation) REFERENCES field(field_designation),
	FOREIGN KEY (material_designation) REFERENCES material_type(material_designation),
	FOREIGN KEY (equipment_designation) REFERENCES equipment_type(equipment_designation)
);
