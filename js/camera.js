/**
 * Camera module - Handles camera access via getUserMedia API
 * Supports front/back camera switching and various resolutions
 */

class CameraManager {
    constructor() {
        this.stream = null;
        this.videoElement = null;
        this.isActive = false;
        this.facingMode = 'environment'; // 'environment' (back) or 'user' (front)
        this.listeners = [];
        this.error = null;
        
        // Available cameras
        this.cameras = [];
        this.currentCameraIndex = 0;
    }

    /**
     * Check if camera is supported
     */
    isSupported() {
        return !!(navigator.mediaDevices && navigator.mediaDevices.getUserMedia);
    }

    /**
     * Get list of available cameras
     */
    async getCameras() {
        try {
            const devices = await navigator.mediaDevices.enumerateDevices();
            this.cameras = devices.filter(device => device.kind === 'videoinput');
            return this.cameras;
        } catch (error) {
            console.error('Error enumerating cameras:', error);
            return [];
        }
    }

    /**
     * Initialize camera with specified video element
     */
    async init(videoElement) {
        if (!this.isSupported()) {
            throw new Error('Camera not supported on this device');
        }

        this.videoElement = videoElement;
        
        // Get available cameras
        await this.getCameras();
        
        // Try to start camera
        await this.start();
        
        return true;
    }

    /**
     * Get ideal constraints for camera
     */
    getConstraints() {
        // Base constraints
        const constraints = {
            video: {
                facingMode: this.facingMode,
                width: { ideal: 1920, min: 1280 },
                height: { ideal: 1080, min: 720 }
            },
            audio: false
        };

        // If we have specific camera devices, try to use them
        if (this.cameras.length > 0) {
            // Try to find a camera matching the desired facing mode
            const targetCamera = this.cameras.find((camera, index) => {
                if (this.facingMode === 'environment') {
                    return camera.label.toLowerCase().includes('back') || 
                           camera.label.toLowerCase().includes('rear') ||
                           camera.label.toLowerCase().includes('environment');
                } else {
                    return camera.label.toLowerCase().includes('front') || 
                           camera.label.toLowerCase().includes('user') ||
                           camera.label.toLowerCase().includes('selfie');
                }
            });

            if (targetCamera) {
                constraints.video.deviceId = { exact: targetCamera.deviceId };
                // Remove facingMode when using deviceId
                delete constraints.video.facingMode;
            }
        }

        return constraints;
    }

    /**
     * Start camera stream
     */
    async start() {
        if (this.isActive && this.stream) {
            return; // Already running
        }

        try {
            // Stop any existing stream
            this.stop();

            const constraints = this.getConstraints();
            this.stream = await navigator.mediaDevices.getUserMedia(constraints);
            
            // Set up video element
            this.videoElement.srcObject = this.stream;
            
            // Wait for video to be ready
            await new Promise((resolve, reject) => {
                this.videoElement.onloadedmetadata = () => {
                    this.videoElement.play()
                        .then(resolve)
                        .catch(reject);
                };
                this.videoElement.onerror = reject;
                
                // Timeout after 10 seconds
                setTimeout(() => reject(new Error('Camera initialization timeout')), 10000);
            });

            this.isActive = true;
            this.error = null;
            this.notifyListeners({ type: 'started', stream: this.stream });
            
        } catch (error) {
            this.error = error;
            this.isActive = false;
            console.error('Error starting camera:', error);
            
            // Try fallback with simpler constraints
            if (error.name === 'OverconstrainedError' || error.name === 'ConstraintNotSatisfiedError') {
                return this.startFallback();
            }
            
            throw error;
        }
    }

    /**
     * Start camera with minimal constraints (fallback)
     */
    async startFallback() {
        try {
            const constraints = {
                video: { facingMode: this.facingMode },
                audio: false
            };

            this.stream = await navigator.mediaDevices.getUserMedia(constraints);
            this.videoElement.srcObject = this.stream;
            
            await new Promise((resolve, reject) => {
                this.videoElement.onloadedmetadata = () => {
                    this.videoElement.play()
                        .then(resolve)
                        .catch(reject);
                };
                setTimeout(() => reject(new Error('Camera initialization timeout')), 10000);
            });

            this.isActive = true;
            this.error = null;
            this.notifyListeners({ type: 'started', stream: this.stream });
            
        } catch (error) {
            this.error = error;
            this.isActive = false;
            console.error('Error starting camera (fallback):', error);
            throw error;
        }
    }

    /**
     * Stop camera stream
     */
    stop() {
        if (this.stream) {
            this.stream.getTracks().forEach(track => {
                track.stop();
            });
            this.stream = null;
        }
        
        if (this.videoElement) {
            this.videoElement.srcObject = null;
        }
        
        this.isActive = false;
        this.notifyListeners({ type: 'stopped' });
    }

    /**
     * Toggle between front and back camera
     */
    async toggleCamera() {
        this.facingMode = this.facingMode === 'environment' ? 'user' : 'environment';
        
        if (this.isActive) {
            await this.start();
        }
        
        return this.facingMode;
    }

    /**
     * Pause camera (keep stream alive but pause video)
     */
    pause() {
        if (this.videoElement) {
            this.videoElement.pause();
        }
    }

    /**
     * Resume camera
     */
    async resume() {
        if (this.videoElement) {
            await this.videoElement.play();
        }
    }

    /**
     * Get current video dimensions
     */
    getVideoDimensions() {
        if (!this.videoElement) return null;
        
        return {
            width: this.videoElement.videoWidth,
            height: this.videoElement.videoHeight
        };
    }

    /**
     * Check if camera is currently active
     */
    isRunning() {
        return this.isActive && this.stream && this.stream.active;
    }

    /**
     * Get the active stream
     */
    getStream() {
        return this.stream;
    }

    /**
     * Get any error that occurred
     */
    getError() {
        return this.error;
    }

    /**
     * Add a listener for camera events
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
     * Notify all listeners
     */
    notifyListeners(event) {
        this.listeners.forEach(callback => {
            try {
                callback(event);
            } catch (error) {
                console.error('Error in camera listener:', error);
            }
        });
    }

    /**
     * Capture a frame from the video
     */
    captureFrame(canvas = null) {
        if (!this.videoElement || !this.isActive) {
            return null;
        }

        // Create canvas if not provided
        if (!canvas) {
            canvas = document.createElement('canvas');
        }

        canvas.width = this.videoElement.videoWidth;
        canvas.height = this.videoElement.videoHeight;

        const ctx = canvas.getContext('2d');
        
        // Apply current transform if needed (for stabilized capture)
        ctx.drawImage(this.videoElement, 0, 0);

        return canvas;
    }

    /**
     * Cleanup
     */
    destroy() {
        this.stop();
        this.listeners = [];
        this.videoElement = null;
    }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = CameraManager;
}
