var drawUtils = new function () {

    this.getDataSourceMapping = function(dataSources)
    {
        var minTime = dataSources[0].minTime;
        var maxTime = dataSources[0].maxTime;
        var dataSourceMap = {};
        dataSources.forEach(function(dataSource)
        {
            maxTime = Math.max(dataSource.maxTime, maxTime);
            minTime = Math.max(dataSource.minTime, minTime);
            dataSourceMap[dataSource.id] = dataSource;
        });
        return {
            dataSourcesMap: dataSourceMap,
            timeRange: {min: minTime, max: maxTime}
        };
    };

    var formNodes = function(rawNodes)
    {
        var nodes = {};
        var nodeIdIpMap = {};
        rawNodes.forEach(function (node) {
            processRawNode(node, nodeIdIpMap, nodes);
        });
        return {nodeMap: nodes, nodeIdIpMap: nodeIdIpMap};
    };
    
    function processRawNode(node, nodeIdIpMap, nodes) {
        nodeIdIpMap[node.id] = node.ip;
        if (nodes[node.ip] == undefined)
        {
            nodes[node.ip] = {
                ip: node.ip,
                variations: [],
                calls: 0
            };
        }
        var foundNode = nodes[node.ip];
        foundNode.calls += node.calls;
        foundNode.variations.push({id:node.id, dataSourceId: node.dataSourceId,
            dns: node.dns, calls: node.calls});
    };

    var formLinks = function (links, nodeIdIpMap) {
        var processedLinkArray = [];
        links.forEach(function (link) {
            processRawLink(link, processedLinkArray, nodeIdIpMap);
        });
        return processedLinkArray;
    };

    function processRawLink(link, processedLinkArray, nodeIdIpMap) {
        var linkSourceIp = nodeIdIpMap[link.sourceId];
        var linkDestinationIp = nodeIdIpMap[link.destinationId];
        var foundLink = processedLinkArray.find(function (existLink) {
            return existLink.source == linkSourceIp &&
                existLink.target == linkDestinationIp;
        });
        if (foundLink == undefined)
        {
            foundLink = {source: linkSourceIp, target: linkDestinationIp, variations:[], calls: 0};
            processedLinkArray.push(foundLink);
        }
        foundLink.variations.push({id: link.id, dataSourceId: link.dataSourceId, calls: link.messagesCount,
            lastMessage: link.lastMessage});
        foundLink.calls += link.messagesCount;
    }

    this.getDrawData = function(linkMessages)
    {
        var graphData = {
            nodesMap:{},
            nodes: [],
            links:[]
        };
        var nodeData = formNodes(linkMessages.nodes);
        graphData.nodesMap = nodeData.nodeMap;
        graphData.nodes = Object.values(nodeData.nodeMap);
        graphData.links = formLinks(linkMessages.links, nodeData.nodeIdIpMap);
        return graphData;
    };


}