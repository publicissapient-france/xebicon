let loadedStore = null;

if (process.env.NODE_ENV === 'production') {
  loadedStore = require('./configureStore.prod');
} else {
  loadedStore = require('./configureStore.dev');
}

export const configureStore = loadedStore;
