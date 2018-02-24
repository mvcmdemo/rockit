app.service('utils', [function(){
    return {
        isEqual: function(element) {
            return element.id === this.normalize();
        },
        isEqualName: function(element) {
            return element.name === this.normalize();
        },
        IDgenerator: function () {
            return Math.round(Math.random()*10000000000);
        }
    };

}]);