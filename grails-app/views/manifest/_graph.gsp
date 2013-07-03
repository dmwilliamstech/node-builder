    <div id="graph"></div>
    <style>

    .node circle {
        fill: #fff;
        stroke: steelblue;
        stroke-width: 1.5px;
    }

    .node {
        font: 10px sans-serif;
    }

    .link {
        fill: none;
        stroke: #ccc;
        stroke-width: 1.5px;
    }

    .balls {
        word-wrap: break-word;
        font: 10px sans-serif;
        word-break: break-all;
        height: 15px;
    }
    </style>
    <body>
    <script src="http://d3js.org/d3.v3.min.js"></script>
    <script>

        var width = 800,
                height = 700;

        var cluster = d3.layout.cluster()
                .size([height, width - 300]);

        var diagonal = d3.svg.diagonal()
                .projection(function(d) { return [d.y, d.x]; });

        var svg = d3.select("#graph").append("svg")
                .attr("width", width)
                .attr("height", height)
                .append("g")
                .attr("transform", "translate(20,0)");

        d3.json(location.pathname.replace(/manifest.*/,"manifest/graph/" + "${manifest.id}"), function(error, root) {
            var nodes = cluster.nodes(root),
                links = cluster.links(nodes);

            var link = svg.selectAll(".link")
                    .data(links)
                    .enter().append("path")
                    .attr("class", "link")
                    .attr("d", diagonal);

            var node = svg.selectAll(".node")
                    .data(nodes)
                    .enter().append("g")
                    .attr("class", "node")
                    .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; })

            node.append("circle")
                    .attr("r", 5);

            node.append("foreignObject")
                    .attr("x", function(d) { return d.children ? -d.name.length * 5 - 20 : +10; })
                    .attr("y", -10)
                    .attr("width", function(d){ return "300px"; })
                    .attr("height", function(d){ return "25px"; })

//                    .style("text-anchor", function(d) { return d.children ? "end" : "start"; })
                    .append("xhtml:p")
                    .attr("title", function(d){return d.name})
                    .attr("class", "balls")
                    .text(function(d) { return d.name/*.replace(/(.{15}).*//*, '$1...')*/ ; });
        });

        d3.select(self.frameElement).style("height", height + "px");

    </script>
