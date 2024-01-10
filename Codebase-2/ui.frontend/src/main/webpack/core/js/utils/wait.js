import Promise from 'promise-polyfill';

export default time => new Promise(resolve => setTimeout(resolve, time));
