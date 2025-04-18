package org.example.plant.realization;

import org.example.plant.protocol.Adjustment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigReader implements Adjustment {
    private static ConfigReader instance;

    public static ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    @Override
    public List<String> readConfigValuesDb() throws IOException {
        List<String> values = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\[\\w+\\] = \\[(.+)\\]"); // Regex для поиска значений

        String filePath = "connect.conf";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    values.add(matcher.group(1)); // Извлекаем текст в скобках
                }
            }
        }
        return values;
    }

    @Override
    public List<String> readConfigValuesMail() {
        String filePath = "mail.conf";
        List<String> values = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Удаляем пробелы и проверяем, что строка не пустая
                line = line.trim();
                if (!line.isEmpty()) {
                    // Извлекаем значение из строки, удаляя ключ и разделители
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String value = parts[1].trim().replaceAll("[\\[\\]]", ""); // Удаляем квадратные скобки
                        values.add(value);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Обработка исключений
        }

        return values;
    }

    public void userRemoval(Connection connection) {
        String filePath = "guru_secession";

        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String username;
            while ((username = br.readLine()) != null) {
                // Получаем ID пользователя по имени
                DataBase base = new DataBase();
                Integer userId = base.getUserIdByUserNameDel(connection, username.trim());
                if (userId != null) {
                    base.clearAssignedTasks(connection, userId); // Очищаем поля ASSIGNED_TASK в TASKS
                    base.deleteUser(connection, userId); // Удаляем данные пользователя
                    base.deleteUserFromBd(connection, username); // Помянем и забудем
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
