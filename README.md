# Text Parser REST API
A Scala NLP application that extracts the specific information from a given English text

## Appendix

### Running

You need to download and install sbt for this application to run.

Once you have sbt installed, the following at the command prompt will start up Play in development mode:

```bash
sbt run
```

Play will start up on the HTTP port at <http://localhost:9000/>.   You don't need to deploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request.

### Usage

If you call the same URL from the command line, you’ll see JSON. Using [httpie](https://httpie.org/), we can execute the command:

```bash
http --verbose http://localhost:9000/textparser/api/nouns/<text>
```

and get back:

```routes
GET /textparser/api/nouns HTTP/1.1
```

```bash
http --verbose http://localhost:9000/textparser/api/verbs/<text>
```

and get back:

```routes
GET /textparser/api/verbs HTTP/1.1
```

```bash
http --verbose http://localhost:9000/textparser/unique/nouns/<text>
```

and get back:

```routes
GET /textparser/api/unique/nouns  HTTP/1.1
```

```bash
http --verbose http://localhost:9000/textparser/unique/verbs/<text>
```

and get back:

```routes
GET /textparser/api/unique/verbs  HTTP/1.1
```

```bash
http --verbose http://localhost:9000/textparser/api/analyze/<text>
```

and get back:
```routes
GET /textparser/api/analyze  HTTP/1.1
```

```bash
http --verbose http://localhost:9000/textparser/api/nameandlocation/<text>
```

and get back:
```routes
GET /textparser/api/nameandlocation  HTTP/1.1
```