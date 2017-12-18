var messagesPopupControl = new function MessagesPopupControl()
{
    var link = {};
    var dataSourceDescription = {};
    var dataTable = $('#modalTable').DataTable({
        "scrollY":        "30vh",
        "scrollCollapse": true,
        "paging":         false,
        select: {
            style: 'single'
        },
        "columnDefs": [
            {
                "targets": [ 3 ],
                "searchable": false
            },
            {
                "targets": [ 4 ],
                "visible": false
            }
        ]
    });

    dataTable.on( 'select', function ( e, dt, type, indexes ) {
        if ( type === 'row' ) {
            $( dataTable.rows().nodes() ).removeClass( 'table-active' );
            if (indexes.length > 0)
            {
                if (previousNum != indexes[0])
                {
                    $( dataTable.rows( indexes ).nodes() ).addClass( 'table-active' );
                }
                selectMessage(indexes[0]);
            }
        }
    } );
    var loadedMessages = {};
    function setModalTitle() {
        $("#modalTitle")[0].innerHTML = link.source.ip + "-" + link.target.ip;
    };

    var previousNum = null;
    function selectMessage(index) {
        $("#messageText").empty();
        if (previousNum == index)
        {
            $('#myCollapsible').collapse('hide');
            previousNum = null;
            return;
        }
        previousNum = index;
        if (index == null)
        {


        }
        console.debug(index);
        $("#messageText")[0].innerHTML = loadedMessages[index + 1].message;
        $('#myCollapsible').collapse('show');


    };


    function addMessage(message, index)
    {
        var num = index;
        loadedMessages[num] = message;
        //var tr = document.createElement('tr');
        var date = new Date(parseInt(message.dateMs));
        var dateText = date.toLocaleString('en-GB');
        var dataSourceName = dataSourceDescription.dataSourcesMap[message.dataSourceId].name;
        var messageBeginning = message.message.substring(0, 50);
        dataTable.rows.add(
            [
            [num, dateText, dataSourceName, messageBeginning, message.message]
            ]
        );
    };

    function generateTableData() {
        var currentMessageNum = 1;
        loadedMessages = {};
        dataTable.rows().remove();
        link.variations.forEach(function (variation) {
            dataloader.loadMessages(variation.id, dataSourceDescription.timeRange.min,
                dataSourceDescription.timeRange.max, function (messages) {
                    var blockIndex = currentMessageNum;
                    currentMessageNum += messages.length;
                    messages.forEach(function(message, index) {addMessage(message, index + blockIndex); });
                    $('#myModal').on('shown.bs.modal', function () {
                        dataTable.columns.adjust().draw(false);
                    })
                    $('#myCollapsible').collapse('hide');
                    $("#messageText")[0].innerHTML = "";

                    $('#myModal').modal({keyboard: true });


                })
        });
    };

    this.updateData = function(linkOut, dataSourceDescriptionOut)
    {
        link = linkOut;
        dataSourceDescription = dataSourceDescriptionOut;
        setModalTitle();
        generateTableData();
    };
}