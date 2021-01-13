"use strict";

import {vueApp} from '@/main.js';
import axios from "axios";

let config = {
  baseURL: process.env.baseURL
};

const _axios = axios.create(config);

_axios.interceptors.request.use(
  function(config) {
      vueApp.$store.commit("setLoading", true)
    return config;
  },
  function(error) {
      vueApp.$store.commit("setLoading", false)
      vueApp.$store.commit("setError", true)
      return Promise.reject(error);
  }
);

// Add a response interceptor
_axios.interceptors.response.use(
  function(response) {
      vueApp.$store.commit("setLoading", false)
    return response;
  },
  function(error) {
      vueApp.$store.commit("setLoading", false)
      vueApp.$store.commit("setError", true)
    return Promise.reject(error);
  }
);

export default _axios;
