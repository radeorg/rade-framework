#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import fitz, math, re, json, sys, requests
from typing import List, Optional

# ---------- 读取 PDF ----------
def open_pdf(pdf_source: str):
    if pdf_source.startswith("http"):
        resp = requests.get(pdf_source, timeout=30)
        resp.raise_for_status()
        return fitz.open(stream=resp.content, filetype="pdf")
    return fitz.open(pdf_source)

# ---------- 计算角度 ----------
def angle_of_char(c1, c2):
    dx = c2["bbox"][0] - c1["bbox"][0]
    dy = c2["bbox"][1] - c1["bbox"][1]
    return math.degrees(math.atan2(dy, dx))

# ---------------- 清除垃圾 ----------------
def strip_head_tail_garbage(lines: List[str]) -> List[str]:
    """
    从页首 & 页尾同时扫描：
    连续 ≥3 行内容相同（允许夹杂极短行）→ 整组丢弃
    """
    if not lines:
        return lines

    def key(l):
        s = l.strip()
        return '@' if 1 <= len(s) <= 2 and s.isalpha() else s

    def _strip_once(lines, reverse=False):
        seq = reversed(lines) if reverse else lines
        keys = [key(l) for l in seq]
        if not keys:
            return lines
        k0 = keys[0]
        cnt = 0
        for k in keys:
            if k in (k0, '@'):
                if k == k0:
                    cnt += 1
            else:
                break
        if cnt >= 3:
            idx = cnt + keys[:cnt].count('@')
            return lines[idx:] if not reverse else lines[:-idx]
        return lines

    lines = _strip_once(lines, reverse=False)   # 页首
    lines = _strip_once(lines, reverse=True)    # 页尾
    return lines


def extract_clean_text(
    pdf_source: str,
    *,
    angle_center: float = 20,
    angle_tol: float = 8,
    discard_start: str = "声明：",
    discard_end: str = "用户ID：",
    extra_discard: Optional[List[str]] = None,
) -> str:
    # 读取 PDF（略）
    doc = open_pdf(pdf_source)
    clean_lines = []
    for page in doc:
        page_lines = []
        for b in page.get_text("rawdict")["blocks"]:
            if b["type"] != 0:
                continue
            for line in b["lines"]:
                chars = []
                for span in line["spans"]:
                    chars.extend(span["chars"])
                if len(chars) < 2:
                    continue
                ang = angle_of_char(chars[0], chars[-1])
                if abs(abs(ang) - angle_center) <= angle_tol:
                    continue
                page_lines.append(''.join(c["c"] for c in chars))
        # ↓↓↓ 仅新增这一行：页首+页尾垃圾行剔除 ↓↓↓
        page_lines = strip_head_tail_garbage(page_lines)
        clean_lines.extend(page_lines)
    doc.close()

    raw = '\n'.join(clean_lines)
    raw = re.sub(rf'{re.escape(discard_start)}[\s\S]*?{re.escape(discard_end)}\d+', '', raw, flags=re.S)
    if extra_discard:
        for s in extra_discard:
            raw = raw.replace(s, "")
    return raw.strip()

def extract_phone_mail(text):
    tel = re.search(r'1[3-9]\d{9}', text)
    mail = re.search(r'[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}', text)
    return {"telephone": tel.group() if tel else "",
            "email": mail.group() if mail else "",
            "content": text}

# txt = extract_clean_text("4.pdf")
# print(json.dumps(extract_phone_mail(txt), ensure_ascii=False, separators=(',', ':')))
if __name__ == "__main__":
    txt = extract_clean_text(sys.argv[1])
    print(json.dumps(extract_phone_mail(txt), ensure_ascii=False, separators=(',', ':')))