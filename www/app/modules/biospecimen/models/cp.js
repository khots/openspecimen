
angular.module('os.biospecimen.models.cp', ['os.common.models'])
  .factory('CollectionProtocol', function(osModel, $http, $q) {
    var CollectionProtocol =
      osModel(
        'collection-protocols',
        function(cp) {
          cp.consentModel = osModel('collection-protocols/' + cp.$id() + '/consent-tiers');
        }
      );

    CollectionProtocol.prototype.getConsentTiers = function() {
      return this.consentModel.query();
    };

    CollectionProtocol.prototype.newConsentTier = function(consentTier) {
      return new this.consentModel(consentTier);
    };


    var cps = {
      "In Transit":['CP1', 'CP2'],
      "Univ of Leicester": ['CP3', 'CP4']
    };

    CollectionProtocol.getCps = function(siteName) {
      //TODO: will fix this with back-end change
      var result = cps[siteName] || [];
      var d = $q.defer();
      d.resolve(result);
      return d.promise;
    }

    return CollectionProtocol;
  });
