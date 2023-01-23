# Java Midyear Project: Research Center Management System
### With SQL database linkage and JavaFX GUI.

<hr>

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
