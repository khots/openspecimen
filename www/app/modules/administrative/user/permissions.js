
angular.module('os.administrative.user.permissions', ['os.administrative.models', 'os.biospecimen.models'])
  .controller('UserPermissionsCtrl', function(
    $scope, $translate, $filter,
    user, permissions,
    PvManager, CollectionProtocol, Role) {
    
    var count = 1;  

    function init() {
      $scope.roles = [];
      $scope.userCPRole = {};
      $scope.permissions = permissions;
      $scope.addMode = false;
      $scope.selectedSiteCps = {};
      $scope.siteCps = {};
      $scope.all = $translate.instant('user.all');
      loadPvs();
    }

    function loadPvs() {
      $scope.sites = PvManager.getSites();

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
      $scope.userCPRole = {};
      setSitePvs($scope.permissions.length);
    }

    $scope.showEditPermission = function(permission, index) {
      $scope.getCps(permission.site);
      setSitePvs(index);
      $scope.userCPRole = angular.copy(permission);
      $scope.editPermissionIdx = index;
      $scope.addMode = false;
    }

    $scope.savePermissions = function() {
      //Save permissions
      $scope.userCPRole.id = count++;
      $scope.permissions.push($scope.userCPRole);
      $scope.userCPRole = {};
      $scope.addMode = false;
    }

    $scope.editPermissions = function() {
      // Update Permission
      $scope.permissions[$scope.editPermissionIdx] = $scope.userCPRole;
      $scope.userCPRole = {};
    }

    $scope.removePermission = function(permission, index) {
      //Remove Permission
      $scope.permissions.splice(index, 1);
    }

    $scope.revertEdit = function() {
      $scope.addMode = false;
      $scope.userCPRole = {};
    }

    $scope.getCps = function(site) {
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
              return ($scope.userCPRole.cp == cp) || selectedCps.indexOf(cp) == -1;
            });
            
            if (selectedCps.length == 0 || (selectedCps.length == 1 && $scope.userCPRole.cp != undefined)) {
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
