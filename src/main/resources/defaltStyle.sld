<?xml version="1.0" encoding="UTF-8"?>
<sld:StyledLayerDescriptor version="1.0.0"
    xmlns:sld="http://www.opengis.net/sld"
    xmlns:ogc="http://www.opengis.net/ogc"
    xmlns:gml="http://www.opengis.net/gml"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.opengis.net/sld http://schemas.opengis.net/sld/1.0.0/StyledLayerDescriptor.xsd"
>

	<sld:NamedLayer>
    <sld:Name>Land</sld:Name>
    <sld:UserStyle>
      <sld:Name>Land Style</sld:Name>
      <sld:IsDefault>1</sld:IsDefault>


	  <sld:FeatureTypeStyle>
        <sld:Rule>
		  <ogc:Filter>
			<ogc:PropertyIsEqualTo>
				<ogc:PropertyName>name</ogc:PropertyName>
				<ogc:Literal>line</ogc:Literal>
			</ogc:PropertyIsEqualTo>
		  </ogc:Filter>
          <sld:LineSymbolizer>
            <sld:Stroke>
              <sld:CssParameter name="stroke">
                <ogc:Literal>#000000</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="stroke-opacity">
                <ogc:Literal>1</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="stroke-width">
                <ogc:Literal>5</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="stroke-dasharray">
                <ogc:Literal>5 2 1 2</ogc:Literal>
              </sld:CssParameter>
            </sld:Stroke>
          </sld:LineSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>


	  <sld:FeatureTypeStyle>
        <sld:Rule>
			<ogc:Filter>
			<ogc:PropertyIsEqualTo>
				<ogc:PropertyName>name</ogc:PropertyName>
				<ogc:Literal>polygon</ogc:Literal>
			</ogc:PropertyIsEqualTo>
		  </ogc:Filter>
          <sld:PolygonSymbolizer>
            <sld:Fill>
              <sld:CssParameter name="fill">#ccffaa</sld:CssParameter>
              <sld:CssParameter name="fill-opacity">
                <ogc:Literal>0.5</ogc:Literal>
              </sld:CssParameter>
            </sld:Fill>
            <sld:Stroke>
              <sld:CssParameter name="stroke">
                <ogc:Literal>#C0C0C0</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="stroke-opacity">
                <ogc:Literal>1</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="stroke-width">
                <ogc:Literal>5</ogc:Literal>
              </sld:CssParameter>
              <sld:CssParameter name="stroke-dasharray">
                <ogc:Literal>3 5 1 5</ogc:Literal>
              </sld:CssParameter>
            </sld:Stroke>
          </sld:PolygonSymbolizer>
        </sld:Rule>
      </sld:FeatureTypeStyle>






    </sld:UserStyle>
  </sld:NamedLayer>

</sld:StyledLayerDescriptor>
