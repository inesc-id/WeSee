var dataloader = new function () {
    var webSocket = null;
    var restInterfaceUrl = window.location.origin + "/graph";
    var graphOnlineUrl = "ws://" + window.location.host + "/graphOnline";
    this.loadDataSources = function (resultRecievedFunction) {
        $.getJSON(restInterfaceUrl + "/dataSources", function(result){
            resultRecievedFunction(result);
        });
    };

    this.loadConnections = function (dataSourceIds, fromDate, toDate, resultRecievedFunction) {
        if (dataSourceIds.length == 0)
        {
            resultRecievedFunction({nodes:[], links:[]});
            return;
        }
        var data = {
            dataSourceIds: dataSourceIds.join(','),
            fromDate: fromDate,
            toDate: toDate
        };
        $.getJSON(restInterfaceUrl + "/connections", data, function(result){
            resultRecievedFunction(result);
        });
    };

    this.loadMessages = function (connectionId, fromDate, toDate, resultRecievedFunction) {

        var data = {
          connectionId: connectionId,
          fromDate: fromDate,
          toDate: toDate
        };
        $.getJSON(restInterfaceUrl + "/connectionOccurrences", data, function(result){
            resultRecievedFunction(result);
        });
    };

    this.setOnlineUpdateHandler = function (handler, context) {
        this.onlineUpdateHandler = handler.bind(context);
    }.bind(this);

    this.onOnlineCheck = function () {
        if (webSocket == null) {
            console.debug(graphOnlineUrl);
            webSocket = new WebSocket(graphOnlineUrl);
            webSocket.onmessage = function (event) {
                if (this.onlineUpdateHandler) {
                    this.onlineUpdateHandler(JSON.parse(event.data));
                }
            }.bind(this);
            webSocket.onerror = function(error) {
                alert("Error " + error.message);
            };
        }
        else {
            webSocket.close();
            webSocket == null;

        }
    };
}