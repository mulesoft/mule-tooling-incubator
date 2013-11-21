'use strict';

function addOnsController($scope, addOnRepositoryService, studioService) {
	$scope.addons = [];
	$scope.findAddOnsFailed = false;

	addOnRepositoryService.findAddOns().
		then(function (data) {
			console.log(data);
			$scope.addons = data;
			$scope.findAddOnsFailed = false;
		}); /*.
		fail(function () {
			$scope.addons = [];
			$scope.findAddOnsFailed = true;
		});*/

	$scope.installAddOn = function (id) {
		studioService.install(id).
			progress(function (n) {
				// marcar progress
				$scope.installProgress = n;
			}).
			then(function () {
				// installed
				$scope.installed = true;
			}).
			fin(function () {
				// limpiar
			});
	};


}
addOnsController.$inject = ['$scope', 'addOnRepositoryService', 'studioService'];

module.exports = addOnsController;