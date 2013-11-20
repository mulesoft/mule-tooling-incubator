'use strict';

var RemoteRepository = require('../../../../lib/remote-repository'),
	repository = new RemoteRepository(require('browser-request'));


module.exports = repository;