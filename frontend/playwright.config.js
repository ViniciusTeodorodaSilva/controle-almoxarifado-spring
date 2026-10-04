import { defineConfig } from '@playwright/test'
export default defineConfig({testDir:'./tests',testMatch:'*.spec.js',fullyParallel:false,workers:1,retries:0,use:{baseURL:'http://localhost:5173',browserName:'chromium',trace:'retain-on-failure'},reporter:'list',timeout:45000})
