var inputControl = new function () {
    var dataSourceUl = $("#data_source_list");
    var minDtSlider = $("#minDt");
    var maxDtSlider = $("#maxDt");
    var minDtText = $("#minDtText");
    var maxDtText = $("#maxDtText");
    var minDateBound = 0;
    var maxDateBound = 0;
    var isDtChanging = false;
    var currentPossibleTimeRange = {min: 0, max: 0};

    this.getCurrentTimeRange = function()
    {
        return currentPossibleTimeRange;
    }

    function drawOptions(dataSourceList)
    {
        dataSourceUl.empty();

        dataSourceList.forEach(function (dataSource) {
            var checkedText = "";
            if (selectedDsIds.indexOf(dataSource.id) >= 0)
                checkedText = "checked";
        "inputControl.onCheck(" + dataSource.id + ")";
           $('<li class="list-group-item">' +
               '<div class="form-check">' +
                '<label class="form-check-label">' +
                    '<input type="checkbox" class="form-check-input" ' +
               '        onchange="inputControl.onCheck(' + dataSource.id + ')" ' + checkedText + ' >'
                    + dataSource.name +
                '</label>' +
               '</div>' +
           '</li>').appendTo(dataSourceUl);
        });
    }
    
    function setTimeLimits(refreshGraphRequired)
    {
        var minTimes = selectedDsIds.map(function (selectedDsId) {
            return dsDescription.dataSourcesMap[selectedDsId].minTime; });
        var maxTimes = selectedDsIds.map(function (selectedDsId) {
            return dsDescription.dataSourcesMap[selectedDsId].maxTime; });
        var minTime = minTimes.reduce(function (prev, next) { return Math.min(prev, next); });
        var maxTime = maxTimes.reduce(function (prev, next) { return Math.max(prev, next); });
        currentPossibleTimeRange.min = minTime;
        currentPossibleTimeRange.max = maxTime;
        isDtChanging = true;
        minDtSlider.attr("min", minTime);
        maxDtSlider.attr("max", maxTime);
        isDtChanging = false;
        syncSliders();
        if (refreshGraphRequired)
            refreshGraph();
    }

    function syncSliders()
    {
        isDtChanging = true;
        if (minDateBound < currentPossibleTimeRange.min)
            minDtSlider[0].value = currentPossibleTimeRange.min;

        if (maxDateBound > currentPossibleTimeRange.max)
            maxDtSlider[0].value = currentPossibleTimeRange.max;

        if (minDateBound > maxDateBound)
            minDtSlider[0].value = maxDateBound;

        if (maxDateBound < minDateBound)
            maxDtSlider[0].value = minDateBound;
        isDtChanging = false;
    }

    function dateToString(date)
    {
        return date.toLocaleString('en-GB');
    };

    this.onTimeChange = function (event) {
        if (event.target === minDtSlider[0])
        {
            minDateBound = parseInt(minDtSlider[0].value);
            maxDtSlider.attr("min", minDateBound);
            minDtText.text(dateToString(new Date(minDateBound)));
        }
        if (event.target === maxDtSlider[0])
        {
            maxDateBound = parseInt(maxDtSlider[0].value);
            minDtSlider.attr("max", maxDateBound);
            maxDtText.text(dateToString(new Date(maxDateBound)));
        }
        syncSliders();
        if (!isDtChanging)
        {
            refreshGraph();
        }
    };

    minDtSlider.change(this.onTimeChange);
    maxDtSlider.change(this.onTimeChange);

    var selectedDsIds = [];
    // dataSourceDescription
    var dsDescription = {};

    var refreshSelectionElements = function (dataSourcesList) {
        selectedDsIds = dataSourcesList.map(function (dataSource) { return dataSource.id; });
    }

    var refreshGraph = function()
    {
        dataloader.loadConnections(selectedDsIds,
            minDateBound, maxDateBound,
            function(rawNodeAndLinks)
            {
                var processedNodeAndLinks = drawUtils.getDrawData(rawNodeAndLinks);
                graph.refresh.bind(graph)(dsDescription, processedNodeAndLinks);
            }
        );

    }

    this.onCheck = function (dataSourceId) {
        var foundIndex = selectedDsIds.indexOf(dataSourceId);
        if (foundIndex < 0)
        {
            selectedDsIds.push(dataSourceId);
        }
        else selectedDsIds.splice(foundIndex, 1);
        if (selectedDsIds.length > 0)
            setTimeLimits(false);
        refreshGraph();
    }

    this.setDataSources = function(dataSourcesDescription)
    {
        dsDescription = dataSourcesDescription;
        var dataSourceList = Object.values(dataSourcesDescription.dataSourcesMap);
        selectedDsIds = dataSourceList.map(function(dataSource)
        {
            return dataSource.id;
        });
        refreshSelectionElements(dataSourceList);
        drawOptions(dataSourceList);
        setTimeLimits(false);
        minDtSlider[0].value = dataSourcesDescription.timeRange.min;
        maxDtSlider[0].value = dataSourcesDescription.timeRange.max;
        maxDtSlider.trigger("change");
        minDtSlider.trigger("change");
        refreshGraph();
    };
    this.onOnlineCheck = function () {
        dataloader.setOnlineUpdateHandler(function (data) {
            this.addNewDataSourcesIfExist(data.dataSources);
            var filteredData = this.filterDataWithSelectedDataSources(data);
            var maxTime = getMaxMessagesTime(filteredData.messages);
            currentPossibleTimeRange.max = maxTime;
            var processedNodeAndLinks = drawUtils.getDrawData(filteredData);
            graph.addAdditionalLinksAndNodes(processedNodeAndLinks);
        }, this);
        dataloader.onOnlineCheck();
    };

    function getMaxMessagesTime(messagesArray) {
        return messagesArray.map(function (message) { return message.dateMs })
            .reduce(function (t1,t2) { return Math.max(t1,t2) });
    }

    this.addNewDataSourcesIfExist = function (dataSources) {
        dataSources.forEach(function (dataSource) {
            if (!dsDescription.dataSourcesMap[dataSource.id])
            {
                dsDescription.dataSourcesMap[dataSource.id] = dataSource;
            }
        });
        var dataSourceList = Object.values(dsDescription.dataSourcesMap);
        drawOptions(dataSourceList);
    };

    this.filterDataWithSelectedDataSources = function(data)
    {
        function isFromDataSource(graphObj) {
            if (selectedDsIds.indexOf(graphObj.dataSourceId) >= 0)
                return true;
            else return false;
        }
        var filteredData = {
          links: [],
          nodes: [],
          messages: []
        };
        data.links.forEach(function (link) {
            if (isFromDataSource(link))
                filteredData.links.push(link);
        });
        data.nodes.forEach(function (node) {
            if (isFromDataSource(node))
                filteredData.nodes.push(node);
        });
        data.messages.forEach(function (message) {
            if (isFromDataSource(message))
                filteredData.messages.push(message);
        });
        return filteredData;
    };

}