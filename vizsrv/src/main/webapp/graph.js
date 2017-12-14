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
        savedDsDescription = dataSourceDescription;
        savedGraphData = processedNodeAndLinks;
        draw();
    };

    var initGraph = function()
    {
        svg.selectAll("*").remove();
        var gRoot = svg.append("g");

        //Zoom functions
        function zoom_actions(){
            gRoot.attr("transform", d3.event.transform)
        }
        //add zoom capabilities
        var zoom_handler = d3.zoom()
            .on("zoom", zoom_actions);
        zoom_handler(svg);
        return gRoot;
    };

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

    function drawLinks(gRoot)
    {
        var link = gRoot.append("g")
            .attr("class", "links")
            .selectAll("line")
            .data(savedGraphData.links)
            .enter().append("line")
            .attr("stroke-width", function(d) { return objectSizes.getLinkWidth(d.calls); })
            .attr("title", function (link) {
                return link.source + "-" + link.target;
            })
            .attr("data-content", function (link) {
                return popupsUtils.generateLinkPopOverMessage(link, savedDsDescription.dataSourcesMap);
            })
            .on("dblclick",function(link){
                messagesPopupControl.updateData(link, savedDsDescription);
            });
        $(".links line").popover({
            container: 'body',
            html: true,
            trigger: 'hover'
        });
        return link;
    };

    function drawNodes(gRoot)
    {
        var nodes = gRoot.append("g")
            .attr("class", "nodes")
            .selectAll("nodes")
            .data(savedGraphData.nodes)
            .enter();

        var node = nodes.append("g")
            .attr("class", "node");

        var circle = node.append("circle")
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
        node.append("text")
            .attr("dy", "1.5em")
            .attr("text-anchor", "middle")
            .text(function(node) { return "ip: " + node.ip; });

        return node;

    };

    function supportTransforms(node,link)
    {
        simulation = d3.forceSimulation(savedGraphData.nodes)
            .force("repelForce", d3.forceManyBody().strength(-500).distanceMin(50))
            .force("link",
                d3.forceLink()
                    .id(function(d) { return d.ip; })
                    .distance(function (d) { return objectSizes.getLinkLength(d.calls)}))
            .force("center", d3.forceCenter(width / 2, height / 2))
            .on("tick", ticked);

        simulation.force("link")
            .links(savedGraphData.links);

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
    };

    var draw = function()
    {
        var gRoot = initGraph();
        var link = drawLinks(gRoot);
        var node = drawNodes(gRoot);
        supportTransforms(node, link);
    };
};