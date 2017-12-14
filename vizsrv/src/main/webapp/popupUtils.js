var popupsUtils = new function () {

    this.generateNodePopOverMessage = function(node, dataSourceMap)
    {
        var emmision = node.variations.map(function(variation)
        {
            var text =
            '<li>' + dataSourceMap[variation.dataSourceId].name +
            '<ul>' +
            '<li>Dns:' + variation.dns + '</li>' +
            '<li>Calls:' + variation.calls + '</li>' +
            '</ul>'+
            '</li>';
            return text;
        }).join();
        var text =
            '<ul>' +
            '   <li>Calls:' + node.calls + '</li>' +
            '   <li>Data sources:' +
            '   <ul>' +
            emmision +
            '   </ul>'
            '</ul>';
        return text;
    }

    this.generateLinkPopOverMessage = function(link, dataSourceMap)
    {
        var text =
            '<ul>' +
            '   <li>Host 1:' + link.source + '</li>' +
            '   <li>Host 2:' + link.target + '</li>' +
            '   <li>Calls:' + link.calls + '</li>' +
            '   <li>Data sources:' +
            '       <ul>' +


            link.variations.map(function(variation)
            {
                var calledDate = new Date(variation.lastMessage.dateMs);
                var text =
                    '<li>' + dataSourceMap[variation.dataSourceId].name +
                        '<ul>' +
                            '<li>Calls:' + variation.calls + '</li>' +
                            '<li>Last message time:' + calledDate.toLocaleString('en-GB') + '</li>' +
                            '<li>Last message beginning:' + variation.lastMessage.message.slice(0, 255) + '</li>' +
                        '</ul>'+
                    '</li>';
                return text;
            }).join() +
            '       </ul>' +
            '   </li>'
        '</ul>';
        return text;
    }
}