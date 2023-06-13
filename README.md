# Java Midyear Project:<br>Research Center Management System

Java program with JavaFX GUI and MySQL database.

## Screenshots
![image](https://github.com/KhalilOuali/Java-Midyear-Project/assets/68998620/3405ed4e-8541-4f54-a76c-c44ee0bd1c82)
![image](https://github.com/KhalilOuali/Java-Midyear-Project/assets/68998620/5c1678eb-630a-479a-82c1-96892b200cc2)
![image](https://github.com/KhalilOuali/Java-Midyear-Project/assets/68998620/f4c79b19-b008-4142-b72b-4a5df0fea7c0)
![image](https://github.com/KhalilOuali/Java-Midyear-Project/assets/68998620/e0e95cab-7a56-4d86-b9ff-9e602e7584f6)
![image](https://github.com/KhalilOuali/Java-Midyear-Project/assets/68998620/dddae8c3-f118-4247-a907-cdc0e64c9a3e)


## Class Diagram
![Classdiagram1](https://user-images.githubusercontent.com/68998620/214056408-043f2596-f87c-43f2-b5bc-e4ec3b72691e.png)

## Relational Model
* `personnel` (personnel_id, full_name, role, login, password)
* `researcher` (#researcher_id, full_name, #experiment_id)
* `field` (field_designation)
* `specialty` (#researcher_id, #field_designation)
* `lab` (lab_id, location)
* `experiment` (experiment_id, #researcher_id, state, code_name, description, #lab_id)
* `material_type` (material_designation, stocked_quantity)
* `equipment_type` (equipment_designation)
* `equipment` (equipment_id, #equipment_designation, #experiment_id)
* `request` (request_id, state, type, #experiment_id, #field_designation, #material_designation, #equipment_designation, quantity, details)
