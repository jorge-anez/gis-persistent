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
    <sld:Name>style-template</sld:Name>
    <sld:UserStyle>
      <sld:Name>layerStyle</sld:Name>
      <sld:IsDefault>1</sld:IsDefault>

	  <sld:FeatureTypeStyle>
          <Rule>
              <ogc:Filter>
                  <ogc:PropertyIsEqualTo>
                      <ogc:PropertyName>geometryType</ogc:PropertyName>
                      <ogc:Literal>Point</ogc:Literal>
                  </ogc:PropertyIsEqualTo>
              </ogc:Filter>
              <PointSymbolizer>
                  <Graphic>
                      <Mark>
                          <WellKnownName>circle</WellKnownName>
                          <Fill>
                              <CssParameter name="fill">${Point.fill}</CssParameter>
                          </Fill>
                      </Mark>
                      <Size>6</Size>
                  </Graphic>
              </PointSymbolizer>
          </Rule>
          <Rule>
              <ogc:Filter>
                  <ogc:PropertyIsEqualTo>
                      <ogc:PropertyName>geometryType</ogc:PropertyName>
                      <ogc:Literal>Line</ogc:Literal>
                  </ogc:PropertyIsEqualTo>
              </ogc:Filter>
              <LineSymbolizer>
                  <Stroke>
                      <CssParameter name="stroke">${Line.stroke}</CssParameter>
                      <CssParameter name="stroke-width">${Line.stroke-width}</CssParameter>
                  </Stroke>
              </LineSymbolizer>
          </Rule>
          <Rule>
              <ogc:Filter>
                  <ogc:PropertyIsEqualTo>
                      <ogc:PropertyName>geometryType</ogc:PropertyName>
                      <ogc:Literal>Polygon</ogc:Literal>
                  </ogc:PropertyIsEqualTo>
              </ogc:Filter>
              <PolygonSymbolizer>
                  <Fill>
                      <CssParameter name="fill">${Polygon.fill}</CssParameter>
                  </Fill>
              </PolygonSymbolizer>
          </Rule>

          <Rule>
              <TextSymbolizer>
                  <Label>
                      <ogc:PropertyName>label</ogc:PropertyName>
                  </Label>
                  <LabelPlacement>
                      <LinePlacement />
                  </LabelPlacement>
                  <Fill>
                      <CssParameter name="fill">${Text.fill}</CssParameter>
                  </Fill>
              </TextSymbolizer>
          </Rule>

      </sld:FeatureTypeStyle>

    </sld:UserStyle>
  </sld:NamedLayer>

</sld:StyledLayerDescriptor>
