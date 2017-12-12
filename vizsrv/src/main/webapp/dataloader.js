var dataloader = new function () {
    var restInterfaceUrl = window.location.origin + "/graph";
    this.loadDataSources = function (resultRecievedFunction) {
        $.getJSON(restInterfaceUrl + "/dataSources", function(result){
            resultRecievedFunction(result);
        });
    }

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
            console.debug(result);
            resultRecievedFunction(result);
        });
    }

    this.loadMessages = function (connectionId, fromDate, toDate, resultRecievedFunction) {
        var data = {
          connectionId: connectionId,
          fromDate: fromDate,
          toDate: toDate
        };
        $.getJSON(restInterfaceUrl + "/connectionOccurrences", data, function(result){
            resultRecievedFunction(result);
        });
    }

}