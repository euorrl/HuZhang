import { createRouter, createWebHistory } from 'vue-router'

import Home from '../pages/Home.vue'
import ExploreRoutes from '../pages/ExploreRoutes.vue'
import Community from '../pages/Community.vue'
import MyTrips from '../pages/MyTrips.vue'
import ReportPath from '../pages/ReportPath.vue'
import Login from '../pages/Login.vue'

const routes = [
    { path: '/', component: Home },
    { path: '/explore', component: ExploreRoutes },
    { path: '/community', component: Community },
    { path: '/trips', component: MyTrips },
    { path: '/report', component: ReportPath },
    { path: '/login', component: Login },
]

export default createRouter({
    history: createWebHistory(),
    routes,
})
