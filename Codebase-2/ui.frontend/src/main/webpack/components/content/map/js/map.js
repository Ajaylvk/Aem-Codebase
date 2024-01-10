import leaflet from 'leaflet';
import AEM from '../../../../core/js/AemComponent';

class Map extends AEM.Component {
    constructor(element) {
        super(element);
        this.compName = this.constructor.name;
        this.name = 'Map';
        this.digitalDataTrackable = true;

        this.api = `https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=${this.element.dataset.apiToken}`;

        this.tileOptions = {
            maxZoom: 18,
            minZoom: 2,
            id: 'mapbox/streets-v11',
        };

        this.mapOptions = {
            worldCopyJump: true,
            maxBounds: [
                [-90, -Infinity],
                [90, Infinity],
            ],
        };
    }

    init() {
        const offices = JSON.parse(this.element.getElementsByTagName('script')[0].innerHTML).locations;
        const initialLatitude = this.element.dataset.initialLatitude;
        const initialLongitude = this.element.dataset.initialLongitude;
        const initialZoom = this.element.dataset.initialZoom;
        const map = leaflet.map('mapid', this.mapOptions).setView([initialLatitude, initialLongitude], initialZoom);

        leaflet.tileLayer(this.api, this.tileOptions).addTo(map);

        const markerIcon = leaflet.divIcon({
            className: 'map__marker',
            iconSize: [25, 30],
            iconAnchor: [12, 28],
            popupAnchor: [0, -28],
        });

        for (let i = 0; i < offices.length; i++) {
            const latitude = offices[i][0];
            const longitude = offices[i][1];
            const text = offices[i][2];
            const linkHref = offices[i][3];
            const linkTarget = offices[i][4];
            const linkText = offices[i][5];

            const popup = `${text === null ? '' : `${text}<br>`}<a href="${linkHref}" target="${linkTarget}">${linkText}</a>`;

            leaflet.marker([latitude, longitude], { icon: markerIcon }).bindPopup(popup).addTo(map);
        }
    }
}

AEM.registerComponent('.map', Map);
