4/9
added a /story postmapping so we can upload prompts to the datastore file using {url}/post/story

can test in postman with a [post request like

{
  "prompt": "Write a story about a dragon who loves to bake cookies."
}

you can create entities from the SDK and see the value by typing {url}/{entity name}

TODO:
1.front end that accepts an input and and pushes it to the datastore table using /post/story endpoint

2. use the Gemeni to take prompts and generate news stories that get uploaded to another table

3. containerize the application with Docker.
