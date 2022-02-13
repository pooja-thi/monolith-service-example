# pooja_thittamaranahalli_backendtasks

This application tries to achieve all the tasks defined by Holobuilder.

## Project Structure

It is a monolith project with both backend and frontend.

- Backend: Kotlin
- Frontend: Angular
- Database:
  - Development: H2 with in-memory persistence
  - Production: PostgreSQL
- Authentication: Jwt authentication
- Language support: English, German

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js][]: I use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [package.json](package.json).

```
npm install
```

I use npm scripts and [Angular CLI][] with [Webpack][] as our build system.

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

```
./gradlew -x webapp
npm start
```

Npm is also used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [package.json](package.json). You can also run `npm update` and `npm install` to manage dependencies.
Add the `help` flag on any command to see how you can use it. For example, `npm help update`.

The `npm run` command will list all the scripts available to run for this project.

## Building for production

### Packaging as jar

To build the final jar and optimize the holobuilder application for production, run:

```
./gradlew -Pprod clean bootJar
```

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

```
java -jar build/libs/*.jar
```

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

## Testing

To launch application's tests, run:

```
./gradlew test integrationTest jacocoTestReport
```

### Client tests

Unit tests are run by [Jest][]. They're located in [src/test/javascript/](src/test/javascript/) and can be run with:

```
npm test
```

## Using Docker to simplify development

To fully dockerize the application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

```
./gradlew bootJar -Pprod jibDockerBuild
```

Then run:

```
docker-compose -f src/main/docker/app.yml up -d
```

[node.js]: https://nodejs.org/
[webpack]: https://webpack.github.io/
[jest]: https://facebook.github.io/jest/
[angular cli]: https://cli.angular.io/
