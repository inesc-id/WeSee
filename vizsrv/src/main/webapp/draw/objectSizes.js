var objectSizes = new function () {
    var previousCalcExponentCalc = {prevMinValue: 0, prevMaxValue:0, a:0};
    // -2^(-x + a) + maxValue = y
    function calcExponental(minValue, maxValue, value)
    {
        if (minValue != previousCalcExponentCalc.prevMinValue ||
            maxValue != previousCalcExponentCalc.prevMaxValue)
        {
            previousCalcExponentCalc.prevMaxValue = maxValue;
            previousCalcExponentCalc.prevMinValue = minValue;
            previousCalcExponentCalc.a = Math.log2(maxValue-minValue);
        }
        return -Math.pow(2, -value/10.0 + previousCalcExponentCalc.a) + maxValue;
    }

    var minLinkWidth = 2;
    var maxLinkWidth = 20;
    this.getLinkWidth = function(calls)
    {
        return calcExponental(minLinkWidth, maxLinkWidth, calls);
    }
    var minLinkLength = 100;
    var maxLinkLength = 1000;
    this.getLinkLength = function(calls)
    {
        return calcExponental(minLinkLength, maxLinkLength, calls);
    }

    var minRadius = 5;
    var maxRadius = 25;
    this.getNodeRadius = function(calls)
    {
        return calcExponental(minRadius, maxRadius, calls);
    }
}
