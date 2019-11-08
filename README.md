## To start application

1. Start docker
    ```
    docker-compose up
    ```
2. Build application
    ```
    gradlew fatJar
    ```

3. Start application (Insert in database)
    ```bash
    java -jar build/libs/neotech-all-1.0-SNAPSHOT.jar
    ```

4. Start application (Show data)
    ```bash
    java -jar build/libs/neotech-all-1.0-SNAPSHOT.jar -p
    ```
