app.service('utils', [function(){
    return {
        isEqual: function(element) {
            return element.id.toString() === this.toString();
        },
        isEqualName: function(element) {
            return element.name.toString() === this.toString();
        },
        IDgenerator: function () {
            return Math.round(Math.random()*10000000000);
        }
    };

}]);