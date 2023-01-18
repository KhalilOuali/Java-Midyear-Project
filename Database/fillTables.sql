-- Active: 1670016658658@@localhost@3306@research_center

INSERT INTO personnel (full_name, role, login, password) VALUES
	("Mister Sir", "Director", "numba", "one"),
	("Mister Perfectionist", "Inventory Manager", "ihave", "ocd"),
	("Archimedes", "Researcher", "greece", "212"),
	("Galileo Galilei", "Researcher", "italy", "1642"),
	("Isaac Newton", "Researcher", "england", "1727"),
	("Benjamin Franklin", "Researcher", "america", "1790"),
	("Marie Curie", "Researcher", "poland", "1934");

INSERT INTO researcher(researcher_id) VALUES (3), (4), (5), (6), (7);

INSERT INTO field(field_designation) VALUES
	("Mechanics"),
	("Optics"),
	("Gravity"),
	("Chemistry"),
	("Electricity"),
	("Radioactivity");

INSERT INTO specialty VALUES
	(3, "Mechanics"),
	(3, "Optics"),
	(4, "Mechanics"),
	(5, "Mechanics"),
	(5, "Gravity"),
	(5, "Optics"),
	(5, "Chemistry"),
	(6, "Electricity"),
	(7, "Chemistry"),
	(7, "Radioactivity");

INSERT INTO lab(location) VALUES
	("East wing, ground floor"),
	("East wing, ground floor"),
	("East wing, first floor"),
	("East wing, first floor"),
	("West wing, ground floor"),
	("West wing, ground floor"),
	("West wing, first floor"),
	("West wing, first floor");

INSERT INTO experiment(researcher_id, state, code_name, description) VALUES
	(3, "Suggested", "Densitometry", "Comparing fluid densities."),
	(4, "Suggested", "Sun Defense", "Focusing light to burn stuff."),
	(5, "Suggested", "Pretty Colors", "Prisms make pretty colors. Wanna test."),
	(7, "Suggested", "Death ray", "Radiation kills. Let's use it..?");

-- Experiment approvals
UPDATE experiment 
SET state = "Ongoing", lab_id = 1
WHERE experiment_id = 1;
UPDATE researcher
SET experiment_id = 1
WHERE researcher_id = 3;

UPDATE experiment 
SET state = "Denied", description = CONCAT(description, "\nDenied: No burning stuff!") 
WHERE experiment_id = 2;

UPDATE experiment 
SET state = "Ongoing", lab_id=5
WHERE experiment_id = 3;

UPDATE researcher
SET experiment_id = 3
WHERE researcher_id = 5;

INSERT INTO material_type VALUES
	("Water", 20),
	("Oil", 10),
	("Paper", 15);

INSERT INTO equipment_type VALUES
	("Fluid container set"),
	("Tetraherdral prism"),
	("Spherical prism");

INSERT INTO equipment(equipment_designation) VALUES
	("Fluid container set"),
	("Fluid container set"),
	("Fluid container set"),
	("Tetraherdral prism"),
	("Tetraherdral prism"),
	("Spherical prism"),
	("Spherical prism");
