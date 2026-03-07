const puppeteer = require('puppeteer');

(async () => {
    const browser = await puppeteer.launch({
        headless: 'new',
        args: ['--no-sandbox', '--disable-setuid-sandbox', '--disable-gpu']
    });
    const page = await browser.newPage();

    await page.goto('file:///home/sufyan/.gemini/antigravity/scratch/campus-complaint-system1/campus-complaint-system/FYP_Chapters_5_6_7.html', {
        waitUntil: 'networkidle0'
    });

    await page.pdf({
        path: '/home/sufyan/.gemini/antigravity/scratch/campus-complaint-system1/campus-complaint-system/FYP_Chapters_5_6_7.pdf',
        format: 'A4',
        margin: { top: '25mm', bottom: '25mm', left: '25mm', right: '25mm' },
        printBackground: true,
        displayHeaderFooter: true,
        headerTemplate: '<div></div>',
        footerTemplate: '<div style="text-align: center; width: 100%; font-size: 10px; color: #666;"><span class="pageNumber"></span></div>'
    });

    await browser.close();
    console.log('PDF generated successfully!');
})();
