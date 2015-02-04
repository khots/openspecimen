
angular.module('os.administrative.user.permissions', ['os.administrative.models', 'os.biospecimen.models'])
  .controller('UserPermissionsCtrl', function(
    $scope, $translate, $filter,
    user, permissions,
    PvManager, CollectionProtocol, Role) {
    
    var count = 1;

    function init() {
      $scope.permission = {};
      $scope.permissions = permissions;
      $scope.addMode = false;
      $scope.all = $translate.instant('user.permission.all');
      loadPvs();
    }

    function loadPvs() {
      $scope.sites = PvManager.getSites();

      $scope.roles = [];
      Role.list().then(
        function(roleList) {
          angular.forEach(roleList, function(role) {
              $scope.roles.push(role.name);
          });
        }
      );
    }

    $scope.showAddPermission = function() {
      $scope.addMode = true;
      $scope.permission = {};
      setSitePvs($scope.permissions.length);
    }

    $scope.showEditPermission = function(permission, index) {
      $scope.getCps(permission.site);
      setSitePvs(index);
      $scope.permission = angular.copy(permission);
      $scope.editPermissionIdx = index;
      $scope.addMode = false;
    }

    $scope.savePermission = function() {
      //TODO: Call REST API to save permission
      $scope.permission.id = count++; // temp id given to saved permssion
      $scope.permissions.push($scope.permission);
      $scope.permission = {};
      $scope.addMode = false;
    }

    $scope.updatePermission = function() {
      //TODO: Call REST API to update permission
      $scope.permissions[$scope.editPermissionIdx] = $scope.permission;
      $scope.permission = {};
    }

    $scope.removePermission = function(index) {
      //TODO: Call REST API to remove permission
      $scope.permissions.splice(index, 1);
    }

    $scope.revertEdit = function() {
      $scope.addMode = false;
      $scope.permission = {};
    }

    $scope.getCps = function(site) {
      $scope.permission.cp = undefined;
      var selectedCps = [];

      var sitePermissions = $filter('filter')($scope.permissions, {site: site});
      angular.forEach(sitePermissions, function(permission) {
        selectedCps.push(permission.cp);
      });

      $scope.cps = (site == $scope.all) ? [$scope.all] : selectedCps.indexOf($scope.all) != -1 ? [] : undefined;

      if (!$scope.cps) {
        CollectionProtocol.getCps(site).then(
          function(list) {
            $scope.cps = list.filter(function(cp) {
              return ($scope.permission.cp == cp) || selectedCps.indexOf(cp) == -1;
            });

            if (selectedCps.length == 0 || (selectedCps.length == 1 && $scope.permission.cp != undefined)) {
               $scope.cps.splice(0, 0, $scope.all);
            }
        });
      }

    }

    function setSitePvs(index) {
      if ($scope.sites.indexOf($scope.all) == -1) {
        $scope.sites.splice(0, 0, $scope.all);
      }
      if (index != 0 || $scope.permissions.length > 1) {
         $scope.sites.splice(0, 1);
      }
    }

    init();
  });
