/**
 * Orientation module - Handles DeviceOrientation API
 * Includes iOS 13+ permission flow and cross-browser compatibility
 */

class OrientationManager {
    constructor() {
        this.isSupported = false;
        this.isActive = false;
        this.hasPermission = false;
        this.listeners = [];
        this.lastOrientation = null;
        
        // Current orientation values
        this.alpha = 0; // Z-axis rotation (0-360)
        this.beta = 0;  // X-axis rotation (-180 to 180)
        this.gamma = 0; // Y-axis rotation (-90 to 90)
        
        // Screen orientation
        this.screenAngle = 0;
        
        // Calibration offset
        this.calibrationOffset = 0;
        this.isCalibrated = false;
        
        this.handleOrientation = this.handleOrientation.bind(this);
        this.handleScreenOrientation = this.handleScreenOrientation.bind(this);
        
        this.checkSupport();
    }

    /**
     * Check if device orientation is supported
     */
    checkSupport() {
        this.isSupported = 'DeviceOrientationEvent' in window;
        
        // Also check for RelativeOrientationSensor as alternative
        if (!this.isSupported && 'RelativeOrientationSensor' in window) {
            this.isSupported = true;
            this.useSensorAPI = true;
        }
        
        return this.isSupported;
    }

    /**
     * Check if permission is required (iOS 13+)
     */
    async requiresPermission() {
        if (typeof DeviceOrientationEvent !== 'undefined' && 
            typeof DeviceOrientationEvent.requestPermission === 'function') {
            return true;
        }
        return false;
    }

    /**
     * Request permission for device orientation (iOS 13+)
     */
    async requestPermission() {
        try {
            if (typeof DeviceOrientationEvent !== 'undefined' && 
                typeof DeviceOrientationEvent.requestPermission === 'function') {
                const permission = await DeviceOrientationEvent.requestPermission();
                this.hasPermission = permission === 'granted';
                return this.hasPermission;
            }
            // Android and other platforms don't require explicit permission
            this.hasPermission = true;
            return true;
        } catch (error) {
            console.error('Error requesting orientation permission:', error);
            this.hasPermission = false;
            return false;
        }
    }

    /**
     * Initialize and start listening for orientation events
     */
    async init() {
        if (!this.isSupported) {
            throw new Error('Device orientation not supported on this device');
        }

        // Check if we need to request permission (iOS)
        const needsPermission = await this.requiresPermission();
        if (needsPermission && !this.hasPermission) {
            const granted = await this.requestPermission();
            if (!granted) {
                throw new Error('Permission denied for device orientation');
            }
        }

        // Start listening
        this.start();
        
        // Listen for screen orientation changes
        if (screen.orientation) {
            screen.orientation.addEventListener('change', this.handleScreenOrientation);
        } else {
            window.addEventListener('orientationchange', this.handleScreenOrientation);
        }
        
        this.handleScreenOrientation();
        
        return true;
    }

    /**
     * Start listening for orientation events
     */
    start() {
        if (this.isActive) return;
        
        window.addEventListener('deviceorientation', this.handleOrientation, true);
        this.isActive = true;
    }

    /**
     * Stop listening for orientation events
     */
    stop() {
        window.removeEventListener('deviceorientation', this.handleOrientation, true);
        this.isActive = false;
    }

    /**
     * Handle device orientation event
     */
    handleOrientation(event) {
        // Get raw values
        this.alpha = event.alpha || 0;
        this.beta = event.beta || 0;
        this.gamma = event.gamma || 0;

        // Store last orientation
        this.lastOrientation = {
            alpha: this.alpha,
            beta: this.beta,
            gamma: this.gamma,
            timestamp: Date.now()
        };

        // Notify listeners
        this.notifyListeners();
    }

    /**
     * Handle screen orientation changes
     */
    handleScreenOrientation() {
        // Get screen orientation angle
        if (screen.orientation) {
            this.screenAngle = screen.orientation.angle || 0;
        } else {
            // Fallback for older browsers
            this.screenAngle = window.orientation || 0;
        }
    }

    /**
     * Calculate the horizon angle based on device orientation
     * Returns the rotation needed to keep the horizon level
     */
    getHorizonAngle() {
        // For horizon lock, we primarily use gamma (rotation around Y axis)
        // when holding the phone in portrait mode
        
        let angle = 0;
        
        // Adjust calculation based on screen orientation
        switch (this.screenAngle) {
            case 0: // Portrait
            case 180: // Portrait upside down
                // Primary roll is gamma, but we need to account for screen angle
                angle = -this.gamma;
                break;
            case 90: // Landscape left
            case -270:
                // In landscape, beta becomes the roll axis
                angle = -this.beta;
                break;
            case -90: // Landscape right
            case 270:
                // In landscape right, beta is inverted
                angle = this.beta;
                break;
            default:
                // Fallback using gamma
                angle = -this.gamma;
        }

        // Apply calibration offset
        if (this.isCalibrated) {
            angle += this.calibrationOffset;
        }

        // Normalize to 0-360
        angle = angle % 360;
        if (angle < 0) angle += 360;

        return angle;
    }

    /**
     * Get the rotation angle for the current screen orientation
     * This returns the angle to counter-rotate the video feed
     */
    getRotationAngle() {
        // Get the horizon angle
        let angle = this.getHorizonAngle();
        
        // Invert for counter-rotation
        return -angle;
    }

    /**
     * Calibrate the current orientation as "level"
     */
    calibrate() {
        // Calculate current horizon angle and store as offset
        const currentAngle = this.getHorizonAngle();
        this.calibrationOffset = -currentAngle;
        this.isCalibrated = true;
    }

    /**
     * Reset calibration
     */
    resetCalibration() {
        this.calibrationOffset = 0;
        this.isCalibrated = false;
    }

    /**
     * Add a listener for orientation changes
     */
    addListener(callback) {
        this.listeners.push(callback);
    }

    /**
     * Remove a listener
     */
    removeListener(callback) {
        const index = this.listeners.indexOf(callback);
        if (index > -1) {
            this.listeners.splice(index, 1);
        }
    }

    /**
     * Notify all listeners of orientation change
     */
    notifyListeners() {
        const data = {
            alpha: this.alpha,
            beta: this.beta,
            gamma: this.gamma,
            screenAngle: this.screenAngle,
            horizonAngle: this.getHorizonAngle(),
            rotationAngle: this.getRotationAngle()
        };

        this.listeners.forEach(callback => {
            try {
                callback(data);
            } catch (error) {
                console.error('Error in orientation listener:', error);
            }
        });
    }

    /**
     * Get current orientation data
     */
    getOrientation() {
        return {
            alpha: this.alpha,
            beta: this.beta,
            gamma: this.gamma,
            screenAngle: this.screenAngle,
            horizonAngle: this.getHorizonAngle(),
            rotationAngle: this.getRotationAngle()
        };
    }

    /**
     * Check if orientation data is available and recent
     */
    isDataAvailable() {
        if (!this.lastOrientation) return false;
        // Check if data is less than 1 second old
        return (Date.now() - this.lastOrientation.timestamp) < 1000;
    }

    /**
     * Cleanup and remove event listeners
     */
    destroy() {
        this.stop();
        this.listeners = [];
        
        if (screen.orientation) {
            screen.orientation.removeEventListener('change', this.handleScreenOrientation);
        } else {
            window.removeEventListener('orientationchange', this.handleScreenOrientation);
        }
    }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = OrientationManager;
}
