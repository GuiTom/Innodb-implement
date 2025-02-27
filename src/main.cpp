#include "table/table.h"
#include <iostream>
#include <vector>
#include <string>

using namespace simple_innodb;

int main() {
    // Define table structure
    std::vector<FieldDef> fields = {
        {"id", FieldType::INT, 4, false, true},
        {"name", FieldType::VARCHAR, 50, false, false},
        {"age", FieldType::INT, 4, true, false},
        {"score", FieldType::FLOAT, 4, true, false}
    };

    // Create student table
    Table student_table("student", fields);
    std::cout << "Student table created successfully!\n";

    // Insert test data
    std::vector<std::vector<std::string>> test_data = {
        {"1", "John", "20", "85.5"},
        {"2", "Mike", "21", "92.0"},
        {"3", "Tom", "19", "78.5"}
    };

    for (const auto& record : test_data) {
        if (student_table.InsertRecord(record)) {
            std::cout << "Record inserted successfully: ID = " << record[0] 
                      << ", Name = " << record[1] << std::endl;
        } else {
            std::cout << "Failed to insert record: ID = " << record[0] << std::endl;
        }
    }

    // Query data
    std::vector<std::string> result;
    if (student_table.FindRecord(2, result)) {
        std::cout << "\nQuery record with ID=2:\n";
        std::cout << "ID: " << result[0] << std::endl;
        std::cout << "Name: " << result[1] << std::endl;
        std::cout << "Age: " << result[2] << std::endl;
        std::cout << "Score: " << result[3] << std::endl;
    } else {
        std::cout << "Record with ID=2 not found\n";
    }

    // Update record
    std::vector<std::string> updated_values = {"2", "Mike", "22", "94.5"};
    if (student_table.UpdateRecord(2, updated_values)) {
        std::cout << "\nRecord with ID=2 updated successfully\n";
        
        // Query again to verify the update
        if (student_table.FindRecord(2, result)) {
            std::cout << "Updated record:\n";
            std::cout << "ID: " << result[0] << std::endl;
            std::cout << "Name: " << result[1] << std::endl;
            std::cout << "Age: " << result[2] << std::endl;
            std::cout << "Score: " << result[3] << std::endl;
        }
    } else {
        std::cout << "Failed to update record\n";
    }

    // Delete record
    if (student_table.DeleteRecord(1)) {
        std::cout << "\nRecord with ID=1 deleted successfully\n";
        
        // Try to query the deleted record
        if (!student_table.FindRecord(1, result)) {
            std::cout << "Confirmed: Record has been deleted\n";
        }
    } else {
        std::cout << "Failed to delete record\n";
    }

    return 0;
}