var width = 960,
    height = 500;

var svg = d3.select("body").append("svg")
    .attr("width", width)
    .attr("height", height);

var color = d3.scaleOrdinal(d3.schemeCategory20);

var simulation = d3.forceSimulation()
    .force("link", d3.forceLink().id(function(d) { return d.id; }))
    .force("charge", d3.forceManyBody())
    .force("center", d3.forceCenter(width / 2, height / 2));

$.ajax({
    method: "GET",
    url: window.location.origin + "/getGraphData"
}).done(function(data) {
    var graph = data;
    var gRoot = svg.append("g");

    //add zoom capabilities
    var zoom_handler = d3.zoom()
        .on("zoom", zoom_actions);
    zoom_handler(svg);
    //Zoom functions
    function zoom_actions(){
        gRoot.attr("transform", d3.event.transform)
    }

    var link = gRoot.append("g")
        .attr("class", "links")
        .selectAll("line")
        .data(graph.links)
        .enter().append("line")
        .attr("stroke-width", function(d) { return Math.sqrt(d.value); });

    var nodes = gRoot.append("g")
        .attr("class", "nodes")
        .selectAll("nodes")
        .data(graph.nodes)
        .enter();

    var node = nodes.append("g")
        .attr("class", "node");

    node.append("circle")
        .attr("r", function(d){ return Math.sqrt(d.calls) + 5;})
        .attr("fill", color(1))
        .call(d3.drag()
            .on("start", dragstarted)
            .on("drag", dragged)
            .on("end", dragended));

    node.append("text")
        .attr("dy", "1.5em")
        .text(function(d) { return d.text; });

    simulation
        .nodes(graph.nodes)
        .on("tick", ticked);

    simulation.force("link")
        .links(graph.links);

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

    function dragstarted(d) {
        if (!d3.event.active) simulation.alphaTarget(0.3).restart();
        d.fx = d.x;
        d.fy = d.y;
    }
    function dragged(d) {
        d.fx = d3.event.x;
        d.fy = d3.event.y;
    }
    function dragended(d) {
        if (!d3.event.active) simulation.alphaTarget(0);
        d.fx = null;
        d.fy = null;
    }
});