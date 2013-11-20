# Development

## Required libraries and tools

### NodeJS and Grunt

You can install NodeJS using [nvm](https://github.com/creationix/nvm):

	curl https://raw.github.com/creationix/nvm/master/install.sh | sh
	nvm install 0.10.22

Then install *grunt-cli*:

	npm install -g grunt-cli
	
### Project dependencies

To do the build using *grunt* you'll need to install the library dependencies first. On this directory execute:

	npm install



## Start a development server

	grunt server
	
The server listens to `*:4001`, and every time that some `.js` file changes it's restarted.

## Prepare client files for production

	grunt client
	
This will create client files for a production environment:

	app/client/all.js
	app/client/styles/all.css
	app/client/index.html
	
These three files is the only thing that you need to use the client page.