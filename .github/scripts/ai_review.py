#!/usr/bin/env python3
# AI Code Review 最小版:读 diff -> 调 DeepSeek -> 输出 review.md(纯标准库,免装依赖)
import json, os, sys, urllib.request

API_KEY = os.environ.get("DEEPSEEK_API_KEY")
if not API_KEY:
    print("错误: DEEPSEEK_API_KEY 环境变量未设置")
    sys.exit(1)
PR_TITLE = os.environ.get("PR_TITLE", "")
PR_URL = os.environ.get("PR_URL", "")

if not os.path.exists("/tmp/pr.diff"):
    print("错误: /tmp/pr.diff 不存在,diff 生成步骤可能失败了")
    sys.exit(1)

try:
    with open("/tmp/pr.diff", encoding="utf-8", errors="replace") as f:
        diff = f.read()
except Exception as e:
    print(f"读取 diff 失败: {e}")
    sys.exit(1)
if len(diff) > 30000:          # 控 token 成本,超大 diff 截断
    diff = diff[:30000] + "\n...[diff 过大已截断]..."

system_prompt = """你是资深 Java 后端工程师,负责 code review。审查下面的 PR diff,只输出 Markdown:

## 🤖 AI Code Review
**总体评价**:2-3 句话(改动是否合理、有没有明显风险)
**问题清单**(按严重度排序,格式: - [Blocker/Suggestion/Nit] 问题描述 (文件名:行号))
**修改建议**:给出关键问题的具体改法

规则:只报真实存在的问题,不吹毛求疵;重点检查:空指针、并发/线程安全、事务边界、SQL 注入、参数校验缺失、边界条件、资源未关闭。如果 diff 基本没问题,就明确说"未发现明显问题",不要硬找。"""

payload = {
    "model": "deepseek-chat",
    "messages": [
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": f"PR 标题:{PR_TITLE}\nPR 链接:{PR_URL}\n\n以下是代码 diff:\n{diff}"},
    ],
    "temperature": 0.3,
    "max_tokens": 2000,
}

req = urllib.request.Request(
    "https://api.deepseek.com/chat/completions",
    data=json.dumps(payload).encode("utf-8"),
    headers={"Content-Type": "application/json", "Authorization": f"Bearer {API_KEY}"},
)
try:
    resp = json.load(urllib.request.urlopen(req, timeout=120))
    text = resp["choices"][0]["message"]["content"]
except Exception as e:
    print(f"AI review 调用失败: {e}")
    sys.exit(1)
with open("/tmp/review.md", "w", encoding="utf-8") as f:
    f.write(text)
print(f"review 已生成:{len(text)} 字")