var inputControl = new function () {

    // dataSourceIds
    var selectedDsIds = [];
    // dataSourceDescription
    var dsDescription = {};

    var refreshSelectionElements = function (dataSourcesMap) {

    }

    var refreshGraph = function()
    {
        dataloader.loadConnections(selectedDsIds,
        dsDescription.timeRange.min, dsDescription.timeRange.max,
            function(rawNodeAndLinks)
            {
                var processedNodeAndLinks = drawUtils.getDrawData(rawNodeAndLinks);
                graph.refresh(dsDescription, processedNodeAndLinks);
            }
        );

    }

    this.setDataSources = function(dataSourcesDescription)
    {
        dsDescription = dataSourcesDescription;
        refreshSelectionElements(dataSourcesDescription);
        var dataSourceArray = Object.values(dataSourcesDescription.dataSourcesMap);
        selectedDsIds = dataSourceArray.map(function(dataSource)
        {
            return dataSource.id;
        });
        refreshGraph();
    }



}