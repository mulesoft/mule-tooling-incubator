/* global document */
'use strict';

var angular = require('angular');


angular.element(document).ready(function () {

	angular.
		module('studio-addons', []).

		factory('studioService', require('./services/studio')).
		factory('addOnRepositoryService', require('./services/repository')).

		controller('addOnListController', require('./controllers/addons'));

	angular.bootstrap(document, ['studio-addons']);

});
