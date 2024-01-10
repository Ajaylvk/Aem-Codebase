import AEM from '../../../../core/js/AemComponent';

class TableV2 extends AEM.Component {
  // eslint-disable-next-line no-useless-constructor
    constructor(element) {
        super(element);
        this.name = 'Table V2';
        this.digitalDataTrackable = true;
    }

  // eslint-disable-next-line class-methods-use-this
    init() {
        let columnvalues;
        $('.HTML_table-wrapper').each(function (index) {
            const tableAlignment = $(this).data('tabletalignment');
            //const tableItemArray = $(this).find('.HTMLtable-item.custom');
            //*called it inside populatedesktop function
            if (
                $(this).width() > 768 ||
        (tableAlignment == 'side-by-side' && $(this).width() > 414)
            ) {
                columnvalues = $(this)
          .data('columnwidth')
          .replaceAll(',', ' ');
                $(this)
          .find('.table-wrapper')
          .css('grid-template-columns', columnvalues);
                $(this)
          .find('.table-mobile-layout')
          .css('display', 'none');
                const noOfCols = $(this).data('noofcolumns');
                const noOfRows = $(this).data('intRowCount');
                $(this)
          .find('.table-wrapper')
          .css('grid-template-rows', `repeat(${noOfRows}, 100%)`);
                $(this)
          .find('.table-wrapper')
          .removeClass('hideTable');
                const rowColors = $(this)
          .data('rowcolors')
          .split(',');
                for (let k = 0; k < rowColors.length; k++) {
                    const row = k * noOfCols;
                    let rowArray = [];
                    rowArray = $(this)
            .find('.HTMLtable-item')
            .slice(row, row + noOfCols);
                    for (let a = 0; a < rowArray.length; a++) {
                        if (rowArray[a] && rowArray[a].classList) {
                            if (!rowArray[a].classList.toString().includes('tableitem')) {
                                rowArray[a].closest('.tableitem-v2').style.backgroundColor =
                  rowColors[k];
                            }
                        }
                    }
                }
                const colColors = $(this)
          .data('colcolors')
          .split(',');
                const colArrays = $(this).find('.HTMLtable-item');
                let columnColor = 0;
                for (let col = 0; col < colArrays.length; col++) {
                    if (
                        colColors[columnColor] != 'NC' &&
            !colArrays[col].classList.toString().includes('tableitem')
                    ) {
                        colArrays[col].closest('.tableitem-v2').style.backgroundColor =
              colColors[columnColor];
                    }
                    columnColor += 1;
                    if (columnColor >= noOfCols) {
                        columnColor = 0;
                    }
                }
                //*moved oringinal code in function and made it only applicable for desktop
                //*earlier this was common for both views
                populatecustomdesktop();
            } else {

                //*assigning row colors in mobile view
                const noOfCols = $(this).data('noofcolumns');
                const rowColors = $(this).data('rowcolors').split(',');
                for (let k = 0; k < rowColors.length; k++) {
                    const row = k * noOfCols;
                    let rowArray = [];
                    rowArray = $(this).find('.HTMLtable-item').slice(row, row + noOfCols);
                    for (let a = 0; a < rowArray.length; a++) {
                        if (rowArray[a] && rowArray[a].classList) {
                            if (!rowArray[a].classList.toString().includes('tableitem')) {
                                rowArray[a].closest('.tableitem-v2').style.backgroundColor =
                        rowColors[k];
                            }
                        }
                    }
                }

                //*column color in mobile view
                const colColors = $(this).data('colcolors').split(',');
                const colArrays = $(this).find('.HTMLtable-item');
                let columnColor = 0;
                for (let col = 0; col < colArrays.length; col++) {
                    if (
                        colColors[columnColor] != 'NC' &&
                  !colArrays[col].classList.toString().includes('tableitem')
                    ) {
                        colArrays[col].closest('.tableitem-v2').style.backgroundColor =
                    colColors[columnColor];
                    }
                    columnColor += 1;
                    if (columnColor >= noOfCols) {
                        columnColor = 0;
                    }
                }
            //*original code 
                $(this)
          .find('.table-mobile-layout')
          .text('');
                const headingObjectLength = $(this)
          .data('columnwidth')
          .split(',').length;
                let tableRowData = [];
                let tableHeadingData = [];
                tableHeadingData = $(this)
          .find('.table-wrapper')
          .children()
          .slice(0, headingObjectLength);
                tableRowData = $(this)
          .find('.table-wrapper')
          .children()
          .slice(headingObjectLength);
                let headLen = 0;
                for (let i = 0; i < tableRowData.length; i++) {
                    let tableMobileWrapper;
                    tableMobileWrapper = document.createElement('div');
                    tableMobileWrapper.classList.add('table-mobile-wrapper');
                    let tableThead;
                    tableThead = document.createElement('div');
                    tableThead.classList.add('table-thead');
                    tableThead.appendChild(tableHeadingData[headLen].cloneNode(true));
                    let tableTdata;
                    tableTdata = document.createElement('div');
                    tableTdata.classList.add('table-tdata');
                    tableTdata.appendChild(tableRowData[i].cloneNode(true));
                    headLen += 1;
                    let noOfCols;
                    noOfCols = $(this).data('noofcolumns');
                    if (headLen >= noOfCols) {
                        headLen = 0;
                    }
                    tableMobileWrapper.appendChild(tableThead);
                    tableMobileWrapper.appendChild(tableTdata);
                    $(this)
            .find('.table-mobile-layout')
            .append(tableMobileWrapper);
                }
                //*for custom mobile color population
                populatecustommobile();
            }
        });
    }


}
//*2 functions added for custom color code 
function populatecustommobile() {

    //*only find custom colored table cells in mobile view inside mobile-wrapper,
    //ignore desktop view table cells  and populate color
    const tableItemArrayMob = $('.HTML_table-wrapper').find('.table-mobile-wrapper .HTMLtable-item.custom');

    if (tableItemArrayMob) {
        for (let i = 0; i < tableItemArrayMob.length; i++) {
            const customvalue = tableItemArrayMob[i].dataset.custom;
            tableItemArrayMob[i].closest(
                '.tableitem-v2'
            ).style.backgroundColor = customvalue;
           
        }
    }
}


function populatecustomdesktop() {
    //*original code
    const tableItemArray = $('.HTML_table-wrapper').find('.HTMLtable-item.custom');
    if (tableItemArray) {
        for (let i = 0; i < tableItemArray.length; i++) {
            const customvalue = tableItemArray[i].dataset.custom;
            tableItemArray[i].closest(
                '.tableitem-v2'
            ).style.backgroundColor = customvalue;
        }
    }
}

AEM.registerComponent('.table-component-v2', TableV2);
