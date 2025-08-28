import os
import codecs
import sys

converted = []
skipped = []
failed = []

for root, dirs, files in os.walk('.'):
    # .git 폴더 제외
    if '.git' in root:
        continue
        
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            
            try:
                # EUC-KR로 읽어보기
                with codecs.open(filepath, 'r', encoding='euc-kr') as f:
                    content = f.read()
                
                # UTF-8로 저장
                with codecs.open(filepath, 'w', encoding='utf-8') as f:
                    f.write(content)
                
                converted.append(filepath)
                print(f"✓ Converted: {filepath}")
                
            except UnicodeDecodeError:
                # 이미 UTF-8이거나 다른 인코딩
                skipped.append(filepath)
                print(f"⊝ Skipped: {filepath} (not EUC-KR)")
                
            except Exception as e:
                failed.append(filepath)
                print(f"✗ Failed: {filepath}: {str(e)}")
                
print(f"변환 완료: {len(converted)}개")
print(f"건너뜀: {len(skipped)}개")
print(f"실패: {len(failed)}개")
