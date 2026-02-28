/**
 * Renderer module - Applies CSS transforms to the video element
 * Handles counter-rotation and zoom for horizon stabilization
 */

class Renderer {
    constructor(videoElement) {
        this.videoElement = videoElement;
        this.smoothing = null;
        
        // Configuration
        this.zoomLevel = 2.0;
        this.smoothingFactor = 0.15;
        this.isStabilizationEnabled = true;
        
        // Current transform state
        this.currentRotation = 0;
        this.targetRotation = 0;
        
        // Animation frame ID
        this.animationFrameId = null;
        this.isRunning = false;
        
        // Initialize smoother
        if (typeof AngleSmoother !== 'undefined') {
            this.smoothing = new AngleSmoother(this.smoothingFactor);
        }
    }

    /**
     * Set zoom level (1.0 - 3.0 recommended)
     * Higher zoom = more coverage but lower quality
     */
    setZoom(level) {
        this.zoomLevel = Math.max(1.0, Math.min(4.0, level));
        this.updateTransform();
    }

    /**
     * Get current zoom level
     */
    getZoom() {
        return this.zoomLevel;
    }

    /**
     * Set smoothing factor
     */
    setSmoothingFactor(factor) {
        this.smoothingFactor = Math.max(0.01, Math.min(1, factor));
        if (this.smoothing) {
            this.smoothing.setSmoothingFactor(this.smoothingFactor);
        }
    }

    /**
     * Get smoothing factor
     */
    getSmoothingFactor() {
        return this.smoothingFactor;
    }

    /**
     * Enable/disable stabilization
     */
    setStabilizationEnabled(enabled) {
        this.isStabilizationEnabled = enabled;
        if (!enabled) {
            // Reset to neutral position when disabled
            this.targetRotation = 0;
            if (this.smoothing) {
                this.smoothing.initialize(0);
            }
        }
    }

    /**
     * Check if stabilization is enabled
     */
    isStabilizationActive() {
        return this.isStabilizationEnabled;
    }

    /**
     * Toggle stabilization
     */
    toggleStabilization() {
        this.setStabilizationEnabled(!this.isStabilizationEnabled);
        return this.isStabilizationEnabled;
    }

    /**
     * Update the target rotation angle
     * This should be called when new orientation data is available
     */
    updateRotation(angle) {
        if (!this.isStabilizationEnabled) {
            this.targetRotation = 0;
            return;
        }

        // Normalize angle to 0-360
        this.targetRotation = angle % 360;
        if (this.targetRotation < 0) {
            this.targetRotation += 360;
        }
    }

    /**
     * Calculate dynamic zoom based on rotation
     * More rotation = need more zoom to cover corners
     */
    calculateDynamicZoom(rotation) {
        // Normalize rotation to 0-90 range (symmetrical)
        const normalizedRotation = Math.abs(((rotation % 180) - 90));
        const angleInRadians = (normalizedRotation * Math.PI) / 180;
        
        // Calculate minimum scale needed to cover corners
        // scale = 1 / cos(angle)
        const minScale = 1 / Math.cos(angleInRadians);
        
        // Use the larger of user zoom or calculated minimum
        return Math.max(this.zoomLevel, minScale);
    }

    /**
     * Apply the transform to the video element
     */
    applyTransform(rotation, zoom) {
        if (!this.videoElement) return;

        // Use counter-rotation (negative) to stabilize
        const counterRotation = -rotation;
        
        // Apply transform with counter-rotation and zoom
        const transform = `rotate(${counterRotation.toFixed(2)}deg) scale(${zoom.toFixed(3)})`;
        this.videoElement.style.transform = transform;
    }

    /**
     * Update transform based on current state
     */
    updateTransform() {
        // Apply smoothing if available
        if (this.smoothing) {
            this.currentRotation = this.smoothing.update(this.targetRotation);
        } else {
            this.currentRotation = this.targetRotation;
        }

        // Calculate zoom (could be dynamic based on rotation)
        const zoom = this.calculateDynamicZoom(this.currentRotation);

        // Apply transform
        this.applyTransform(this.currentRotation, zoom);
    }

    /**
     * Animation loop for smooth updates
     */
    animate() {
        if (!this.isRunning) return;

        this.updateTransform();
        this.animationFrameId = requestAnimationFrame(() => this.animate());
    }

    /**
     * Start the render loop
     */
    start() {
        if (this.isRunning) return;
        
        this.isRunning = true;
        this.animate();
    }

    /**
     * Stop the render loop
     */
    stop() {
        this.isRunning = false;
        
        if (this.animationFrameId) {
            cancelAnimationFrame(this.animationFrameId);
            this.animationFrameId = null;
        }
    }

    /**
     * Reset to neutral position
     */
    reset() {
        this.targetRotation = 0;
        this.currentRotation = 0;
        if (this.smoothing) {
            this.smoothing.reset();
        }
        this.updateTransform();
    }

    /**
     * Get current rotation angle
     */
    getCurrentRotation() {
        return this.currentRotation;
    }

    /**
     * Get target rotation angle
     */
    getTargetRotation() {
        return this.targetRotation;
    }

    /**
     * Capture the current stabilized frame
     */
    captureFrame(canvas = null) {
        if (!this.videoElement) return null;

        if (!canvas) {
            canvas = document.createElement('canvas');
        }

        const width = this.videoElement.videoWidth;
        const height = this.videoElement.videoHeight;
        
        canvas.width = width;
        canvas.height = height;
        
        const ctx = canvas.getContext('2d');
        
        // Save context state
        ctx.save();
        
        // Move to center
        ctx.translate(width / 2, height / 2);
        
        // Apply counter-rotation
        ctx.rotate((-this.currentRotation * Math.PI) / 180);
        
        // Apply zoom
        const zoom = this.calculateDynamicZoom(this.currentRotation);
        ctx.scale(zoom, zoom);
        
        // Draw video centered
        ctx.drawImage(this.videoElement, -width / 2, -height / 2, width, height);
        
        // Restore context
        ctx.restore();
        
        return canvas;
    }

    /**
     * Cleanup
     */
    destroy() {
        this.stop();
        this.videoElement = null;
        this.smoothing = null;
    }
}

// Export for use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = Renderer;
}
