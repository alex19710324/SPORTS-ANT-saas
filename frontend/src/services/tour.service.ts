import { driver } from 'driver.js';
import 'driver.js/dist/driver.css';

class TourService {
    private driverObj: any;

    constructor() {
        this.driverObj = driver({
            showProgress: true,
            steps: [
                { element: '.logo', popover: { title: 'Welcome', description: 'Welcome to SPORTS ANT SaaS! This is your new command center.' } },
                { element: '.el-menu-vertical-demo', popover: { title: 'Navigation', description: 'Access all your modules here. The menu changes based on your role.' } },
                { element: '.message-badge', popover: { title: 'Notifications', description: 'Check system alerts and messages here.' } },
                { element: '.user-profile', popover: { title: 'Profile', description: 'Manage your account settings and logout.' } }
            ]
        });
    }

    public startTour() {
        this.driverObj.drive();
    }
}

export default new TourService();
