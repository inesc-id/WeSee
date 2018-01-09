var graph = new function () {
    var svg = d3.select("#svgDiv svg");
    var width = 1200;
    var height = 800;
    var color = d3.scaleOrdinal(d3.schemeCategory20);
    var savedDsDescription = {};
    var savedGraphData = {};

    this.resize = function()
    {
        svg.attr("width", width).attr("height", height);
    }

    this.resize();
    var simulation = {};

    this.refresh = function(dataSourceDescription, processedNodeAndLinks)
    {
        svg.selectAll("*").remove();
        savedDsDescription = dataSourceDescription;
        savedGraphData = processedNodeAndLinks;
        initGraph.bind(this)();
        restart.bind(this)();

    };
    var g, link, node;
    var initGraph = function()
    {
        simulation = d3.forceSimulation(savedGraphData.nodes)
            .force("charge", d3.forceManyBody().strength(-1000))
            .force("link",
                d3.forceLink(savedGraphData.links)
                    .id(function (d) {return d.ip;})
                    .distance(function (d) { return objectSizes.getLinkLength(d.calls)}))
            .force("x", d3.forceX())
            .force("y", d3.forceY())
            .on("tick", ticked);

        g = svg.append("g").attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");
        link = g.append("g").selectAll(".link");
        node = g.append("g").selectAll(".node");

        //Zoom functions
        function zoom_actions(){
            g.attr("transform", d3.event.transform)
        }
        //add zoom capabilities
        var zoom_handler = d3.zoom()
            .on("zoom", zoom_actions);
        zoom_handler(svg);
    };

    function restart() {
        node = node.data(savedGraphData.nodes, function(d) { return d.id;});
        node.exit().remove();
        var gNode = node.enter()
            .append("g")
            .attr("class", "node");
        gNode.append("circle")
            .attr("fill", color(1))
            .attr("r", function(node){ return objectSizes.getNodeRadius(node.calls);})
            .attr("data-content", function (node) { return popupsUtils.generateNodePopOverMessage(node, savedDsDescription.dataSourcesMap); })
            .call(d3.drag()
                .on("start", dragstarted)
                .on("drag", dragged)
                .on("end", dragended));
        gNode.append("text")
            .attr("dy", "1.5em")
            .attr("text-anchor", "middle")
            .style('fill', 'black')
            .text(function(node) { return "ip: " + node.ip; });

        node = node.merge(gNode);
        $('.node circle').popover({
            container: 'body',
            html: true,
            trigger: 'hover'
        });

        // Apply the general update pattern to the links.
        link = link.data(savedGraphData.links, function(d) { return d.source.ip + "-" + d.target.ip; });
        link.exit().remove();
        link = link.enter()
            .append("line")
            .attr("stroke-width", function(d) { return objectSizes.getLinkWidth(d.calls); })
            .attr("data-content", function (link) {
                return popupsUtils.generateLinkPopOverMessage(link, savedDsDescription.dataSourcesMap);
            })
            .on("dblclick",function(link){
                messagesPopupControl.updateData(link, savedDsDescription);
            })
            .merge(link);
        $("line").popover({
            container: 'body',
            html: true,
            trigger: 'hover'
        });
        // Update and restart the simulation.
        simulation.nodes(savedGraphData.nodes);
        simulation.force("link").links(savedGraphData.links);
        simulation.alpha(1).restart();
    }

    function dragstarted(d) {
        if (!d3.event.active) simulation.alphaTarget(0.3).restart();
        d.fx = d.x;
        d.fy = d.y;
    };
    function dragged(d) {
        d.fx = d3.event.x;
        d.fy = d3.event.y;
    };
    function dragended(d) {
        if (!d3.event.active) simulation.alphaTarget(0);
        d.fx = null;
        d.fy = null;
    };

    function drawLinks(gRoot, additionalLinks, isFirstTimeDraw)
    {
        var link = {};

        // link = gRoot.append("g")
        //     .attr("class", "links")
        //     .selectAll("line")
        //     .data(additionalLinks)
        //     .enter().append("line")
        //     .attr("stroke-width", function(d) { return objectSizes.getLinkWidth(d.calls); })
        //     .attr("title", function (link) {
        //         return link.source + "-" + link.target;
        //     })
        //     .attr("data-content", function (link) {
        //         return popupsUtils.generateLinkPopOverMessage(link, savedDsDescription.dataSourcesMap);
        //     })
        //     .on("dblclick",function(link){
        //         messagesPopupControl.updateData(link, savedDsDescription);
        //     });


        $(".links line").popover({
            container: 'body',
            html: true,
            trigger: 'hover'
        });
        return link;
    };


    function drawNodes(gRoot, additionalNodes, isFirstTimeDraw)
    {
        var nodes = new Object();

        nodes = gRoot
            .selectAll("nodes")
            .data(additionalNodes)
            .enter()
            .append("g")
            .attr("class", "nodes")
            .attr("id", function (d) {
                return d.ip;
            });

        var circle = nodes.append("circle")
            .attr("r", function(node){ return objectSizes.getNodeRadius(node.calls);})
            .attr("fill", color(1))
            .attr("title", function (node) { return node.ip; })
            .attr("data-content", function (node) { return popupsUtils.generateNodePopOverMessage(node, savedDsDescription.dataSourcesMap); })
            .call(d3.drag()
                .on("start", dragstarted)
                .on("drag", dragged)
                .on("end", dragended));
        $('.node circle').popover({
            container: 'body',
            html: true,
            trigger: 'hover'
        });
        // nodes.append("text")
        //     .attr("dy", "1.5em")
        //     .attr("text-anchor", "middle")
        //     .text(function(node) { return "ip: " + node.ip; });

        return nodes;

    };

    // function supportTransforms(dataNodes,dataLinks, graphNodes, graphLinks)
    // {
    //     simulation = d3.forceSimulation(savedGraphData.nodes)
    //         .force("charge", d3.forceManyBody().strength(-1000))
    //         .force("link", d3.forceLink(savedGraphData.links).distance(200).id(function(d) { return d.ip; }))
    //         .force("x", d3.forceX())
    //         .force("y", d3.forceY())
    //         .alphaTarget(1)
    //         .on("tick", ticked);
    //         // .force("repelForce", d3.forceManyBody().strength(-500).distanceMin(50))
    //         // .force("link",
    //         //     d3.forceLink()
    //         //         .id(function(d) { return d.ip; })
    //         //         .distance(function (d) { return objectSizes.getLinkLength(d.calls)}))
    //         // .force("center", d3.forceCenter(width / 2, height / 2))
    //         // .on("tick", ticked.bind(this));
    //     simulation.force("link")
    //         .links(savedGraphData.links);
    // };

    function ticked() {
        link
            .attr("x1", function(d) { return d.source.x; })
            .attr("y1", function(d) { return d.source.y; })
            .attr("x2", function(d) { return d.target.x; })
            .attr("y2", function(d) { return d.target.y; });

        node.attr("transform", function (d) {
            return "translate(" + d.x + "," + d.y + ")";
        });
    }

    this.addAdditionalLinksAndNodes = function (data)
    {
        var drawData = findOrAddGraphData.bind(this)(data);
        if (drawData.nodes.length == 0 && drawData.links.length == 0)
            return;
        savedGraphData.nodes = savedGraphData.nodes.concat(drawData.nodes);
        savedGraphData.links = savedGraphData.links.concat(drawData.links);

        restart();

        simulation.nodes(savedGraphData.nodes)
        simulation.force("link").links(savedGraphData.links);
        simulation.alpha(1).restart();
    };

    function findOrAddGraphData(data) {
        var drawElements = {links: [], nodes: []};
        data.links.forEach(function (newLink) {
            var foundExistingLink = savedGraphData.links.find(
                function (existLink) {
                    return existLink.source.ip == newLink.source && existLink.target.ip == newLink.target;
                });
            if (foundExistingLink == null)
            {
                drawElements.links.push(newLink);
            }
            else
            {
                updateLinkVariations(newLink, foundExistingLink);
                updateLinkGraph(foundExistingLink);
            }
        });
        data.nodes.forEach(function (newNode) {
            var foundExistingNode = savedGraphData.nodes.find(
                function (existNode) {
                    return existNode.ip == newNode.ip;
                });
            if (foundExistingNode == null)
            {
                savedGraphData.nodesMap[newNode.ip] = newNode;
                drawElements.nodes.push(newNode);
            }
            else
            {
                updateNodeVariations(newNode, foundExistingNode);
                updateNodeGraph(foundExistingNode);
            }
        });
        return drawElements;
    }

    function updateLinkVariations(newLink, foundExistingLink) {
        newLink.variations.forEach(function (variation) {
            var foundVariation = foundExistingLink.variations.find(function (existVariation) {
                return existVariation.id == variation.id;
            });
            if (!foundVariation)
            {
                foundExistingLink.variations.push(variation);
                foundExistingLink.calls += variation.calls;
            }
            else
            {
                var callsDiff = variation.calls + foundVariation.calls;
                foundExistingLink.calls += callsDiff;
                foundVariation.calls = variation.calls;
            }
        });
    }

    function updateNodeVariations(newNode, foundExistingNode) {
        newNode.variations.forEach(function (variation) {
            var foundVariation = foundExistingNode.variations.find(function (existVariation) {
                return existVariation.id == variation.id;
            });
            if (!foundVariation)
            {
                foundExistingNode.variations.push(variation);
                foundExistingNode.calls += variation.calls;
            }
            else
            {
                var callsDiff = variation.calls + foundVariation.calls;
                foundExistingNode.calls += callsDiff;
                foundVariation.calls = variation.calls;
            }
        });
    };

    function updateLinkGraph(updatedLinkData) {
        link.filter(function (d) {
            return d.source == updatedLinkData.source.ip && d.target == updatedLinkData.target.ip; })
            .attr("stroke-width", function() { return objectSizes.getLinkWidth(updatedLinkData.calls); })
            .attr("data-content", function () {
                return popupsUtils.generateLinkPopOverMessage(updatedLinkData, savedDsDescription.dataSourcesMap);
            });
    }

    function updateNodeGraph(updatedNodeData) {
        node.selectAll("circle").filter(function (d) {
            return d.ip == updatedNodeData.ip;})
            .attr("r", function(){ return objectSizes.getNodeRadius(updatedNodeData.calls);})
            .attr("data-content", function () {
                return popupsUtils.generateNodePopOverMessage(updatedNodeData, savedDsDescription.dataSourcesMap);
            });
    }
};