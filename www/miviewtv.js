/*global cordova, module*/
  
module.exports = {
    channels: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "MiViewTVPlugin", "channels", []);
    }
};
