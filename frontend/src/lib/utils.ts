import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

/**
 * 格式化日期时间为 yyyy-MM-dd HH:mm:ss
 * @param date 可以是 Date 对象、时间戳（数字）或日期字符串
 * @returns 格式化后的字符串
 */
export function formatDateTime(date: Date | number | string | undefined | null): string {
  if (date === undefined || date === null || date === "") {
    return "-";
  }

  const d = new Date(date);
  
  // 检查无效日期
  if (isNaN(d.getTime())) {
    return "-";
  }

  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  const seconds = String(d.getSeconds()).padStart(2, '0');

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}
