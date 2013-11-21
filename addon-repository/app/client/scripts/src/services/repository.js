'use strict';

var RemoteRepository = require('../../../../../lib/remote-repository');

function repositoryServiceFactory($http) {
	return new RemoteRepository($http);
}
repositoryServiceFactory.$inject = ['$http'];

module.exports = repositoryServiceFactory;
