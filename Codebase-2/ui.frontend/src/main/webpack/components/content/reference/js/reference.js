import $ from 'jquery';
import Dropkick from 'dropkickjs';
import AEM from '../../../../core/js/AemComponent';
import RenderTemplate from '../../../../core/js/utils/handlebar-renderer';

class ReferenceFilters extends AEM.Component {

    constructor(element) {
        super(element);
        this.compName = this.constructor.name;
        this.name = 'Reference Filter Container';
        this.digitalDataTrackable = true;
        this.title = null;
        this.$titleComponent = null;
        this.tileData = [];
        this.render = new RenderTemplate();
        this.pageType = $(element).data('page-type');
        this.pagePath = $(element).data('current-path') || '';
        this.loadMoreBtn = $(element).closest('.filtercontainer__list').find('.reference__load-more');
        this.defaultCount = $(element).closest('.filtercontainer__container').data('load-default-items') || 10;
        this.loadMoreCount = $(element).closest('.filtercontainer__container').data('load-more-items') || 10;
        this.currentCount = 0;
    }

    init() {
        this.$filterVals = {
            1: {
                type: '',
                defaultVal: '',
                defaultTitle: '',
                selectedVal: '',
                selectedTitle: '',
                dropkickRef: () => {
                },
            },
            2: {
                type: '',
                defaultVal: '',
                defaultTitle: '',
                selectedVal: '',
                selectedTitle: '',
                dropkickRef: () => {
                },
            },
            3: {
                type: '',
                defaultVal: '',
                defaultTitle: '',
                selectedVal: '',
                selectedTitle: '',
                dropkickRef: () => {
                },
            },
        };
        this.$refWrapper = null;
        this.filterParentEle = $('.filtercontainer__wrapper');
        this.filterClasses = ['.filtercontainer__filter-1', '.filtercontainer__filter-2', '.filtercontainer__filter-3'];
        this.filterIds = ['#selectBox1', '#selectBox2', '#selectBox3'];
        this._attachEvents();
        this._attachFilterTypes();
        this._fetchData(true);
    }

    _attachEvents() {
        this.loadMoreBtn.on('click', this._loadMoreItems.bind(this));
    }

    _attachFilterTypes() {
        if (this.$filterVals) {
            const filter1Type = this.filterParentEle.find('#filter1_ref');
            const filter2Type = this.filterParentEle.find('#filter2_ref');
            const filter3Type = this.filterParentEle.find('#filter3_ref');

            const filterList = [filter1Type, filter2Type, filter3Type];

            filterList.forEach((item, index) => {
                const hiddenVal = item.val();
                if (hiddenVal !== undefined) {
                    this.$filterVals[index + 1].type = `${hiddenVal}@`;
                    this.$filterVals[index + 1].defaultVal = hiddenVal.split(':')[1];
                    this.$filterVals[index + 1].defaultTitle = item.data('title');
                    this.$filterVals[index + 1].selectedVal = hiddenVal.split(':')[1];
                    this.$filterVals[index + 1].selectedTitle = item.data('title');
                }
            }, this);
        }
    }

    _paintDOMdata(list, firstLoad) {
        // render template
        this.render.init($('.reference__wrapper'), { result: list, pageType: this.pageType }, !firstLoad);
    }

    _renderFilterOptions(firstTime) {
        const filterData = this.tileData;
        const filterOptions = filterData.filterList || {};
        const filterOptionMap = {
            0: 'filterOptionOneList',
            1: 'filterOptionTwoList',
            2: 'filterOptionThreeList',
        };

        this.filterClasses.forEach((item, index) => {
            const self = this;
            const currentFilterObj = self.$filterVals[index + 1];
            const currentOptions = filterOptions[filterOptionMap[index]];
            const selectBoxClass = `select${item}`;
            const selectBoxEle = $(selectBoxClass);
            const defaultVal = currentFilterObj.defaultVal;
            const defaultTitle = currentFilterObj.defaultTitle;

            if (selectBoxEle.length < 1) return;

            if (currentOptions) {
                let allOptions = [];
                if (defaultVal !== '') {
                    allOptions.push({ title: defaultTitle, value: defaultVal });
                }
                allOptions = [...allOptions, ...currentOptions];

                // add selected attribute to option
                allOptions = allOptions.map(opt => ((opt.value === currentFilterObj.selectedVal)
                    ? { ...opt, selected: true } : opt));

                // render template
                this.render.init(selectBoxEle, { result: allOptions });
            }

            if (firstTime) {
                /* eslint-disable no-new */
                // initialise dropkick dropdowns
                currentFilterObj.dropkickRef = new Dropkick(this.filterIds[index], {
                    mobile: false,
                    change() {
                        currentFilterObj.selectedTitle = this.selectedOptions[0].getAttribute('text');
                        currentFilterObj.selectedVal = this.value;
                        self._fetchData();
                    },
                });
            } else {
                // refresh drop kick
                currentFilterObj.dropkickRef.refresh();
            }
        }, this);
    }

    _loadMoreItems(firstLoad) {
        const fullList = (this.tileData.resultList || []);
        const fullListLength = fullList.length;
        const startIndx = this.currentCount;
        const isFirstLoad = (firstLoad === true);
        let endIndx = isFirstLoad ? this.defaultCount : this.loadMoreCount;
        let loadMoreList = [];

        endIndx = startIndx + endIndx;
        loadMoreList = fullList.slice(startIndx, endIndx);

        this._paintDOMdata(loadMoreList, isFirstLoad);
        this.currentCount = endIndx;

        if (this.currentCount < fullListLength) {
            this.loadMoreBtn.removeClass('hide');
        } else {
            this.loadMoreBtn.addClass('hide');
        }
    }

    _fetchData(firstTime) {
        const pathRef = this.pagePath.replaceAll('/', '@');
        let url = '';
        const filter = [];
        let filterUrl = '';

        if (firstTime) {
            url = `/bin/reference.${pathRef}.all.json`;
        } else {
            Object.keys(this.$filterVals).forEach((v, i) => {
                let val = '';
                const conditionVal = (this.$filterVals[i + 1].type !== '')
                    ? this.$filterVals[i + 1].type.split(':')[1].replace('@', '')
                    : '';
                if (this.$filterVals[i + 1].selectedVal.toLowerCase()
                    === conditionVal.toLowerCase()) {
                    val = '=';
                } else val = `${this.$filterVals[i + 1].type}${this.$filterVals[i + 1].selectedVal}`;
                if (val !== '=') {
                    filter.push(val);
                }
            });

            filterUrl = filter.join('.') === '' ? 'all' : filter.join('.').replaceAll('/', '@');

            url = `/bin/reference.${pathRef}.${filterUrl}.json`;
        }

        $.get(url).then((response) => {
            // const ifAllJson = !!((firstTime || filterUrl === 'all'));
            this.tileData = response;

            // reset load more count
            this.currentCount = 0;

            this._loadMoreItems(true);
            this._renderFilterOptions(firstTime);
        }).catch((error) => {
            console.log(error); // eslint-disable-line no-console
        });
    }
}

AEM.registerComponent('.referenceComponent', ReferenceFilters);
