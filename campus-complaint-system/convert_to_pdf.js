const fs = require('fs');
const { execSync } = require('child_process');

let html = fs.readFileSync('/home/sufyan/.gemini/antigravity/scratch/campus-complaint-system1/campus-complaint-system/FYP_Chapters_5_6_7.html', 'utf8');

const style = `
<style>
  body {
    font-family: 'Times New Roman', serif;
    font-size: 12pt;
    line-height: 1.6;
    color: #333;
    max-width: 800px;
    margin: 0 auto;
    padding: 40px;
  }
  h1 { font-size: 22pt; margin-top: 40px; border-bottom: 2px solid #333; padding-bottom: 10px; }
  h2 { font-size: 16pt; margin-top: 30px; }
  h3 { font-size: 14pt; margin-top: 20px; }
  h4 { font-size: 13pt; margin-top: 15px; }
  table { border-collapse: collapse; width: 100%; margin: 15px 0; font-size: 10pt; }
  th, td { border: 1px solid #666; padding: 8px 10px; text-align: left; }
  th { background-color: #f0f0f0; font-weight: bold; }
  code { background: #f4f4f4; padding: 2px 5px; border-radius: 3px; font-size: 10pt; }
  pre { background: #f4f4f4; padding: 15px; border-radius: 5px; overflow-x: auto; font-size: 10pt; }
  pre code { background: none; padding: 0; }
  hr { border: none; border-top: 2px solid #ccc; margin: 30px 0; }
  @media print {
    body { padding: 0; }
    h1 { page-break-before: always; }
    h1:first-child { page-break-before: avoid; }
    table { page-break-inside: avoid; }
  }
</style>
`;

html = html.replace('</head>', style + '</head>');
fs.writeFileSync('/home/sufyan/.gemini/antigravity/scratch/campus-complaint-system1/campus-complaint-system/FYP_Chapters_5_6_7.html', html);
console.log('Styled HTML written successfully');
