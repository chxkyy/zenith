import React, { useEffect, useState } from 'react';
import { CheckCircle, X } from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';

interface NotificationProps {
  message: string;
  type?: 'success' | 'error' | 'info';
  duration?: number;
}

export default function Notification({ message, type = 'success', duration = 3000 }: NotificationProps) {
  const [isVisible, setIsVisible] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsVisible(false);
    }, duration);

    return () => clearTimeout(timer);
  }, [duration]);

  return (
    <AnimatePresence>
      {isVisible && (
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -20 }}
          transition={{ duration: 0.3 }}
          className={`fixed top-4 right-4 z-50 flex items-center gap-3 px-4 py-3 rounded-lg shadow-lg ${type === 'success' ? 'bg-green-50 text-green-700' : type === 'error' ? 'bg-red-50 text-red-700' : 'bg-blue-50 text-blue-700'}`}
        >
          {type === 'success' && <CheckCircle size={20} className="text-green-600" />}
          <span className="text-sm font-medium">{message}</span>
          <button 
            onClick={() => setIsVisible(false)}
            className="p-1 hover:bg-white/50 rounded-full transition-colors"
          >
            <X size={16} className="text-slate-400" />
          </button>
        </motion.div>
      )}
    </AnimatePresence>
  );
}